package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.StageRequest;
import tn.poste.gestionstages.dto.StageResponse;
import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.enums.TypeStage;
import tn.poste.gestionstages.exception.BusinessException;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.CandidatureRepository;
import tn.poste.gestionstages.repository.EncadrantRepository;
import tn.poste.gestionstages.repository.StageRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final CandidatureRepository candidatureRepository;
    private final AffectationRepository affectationRepository;
    private final EncadrantRepository encadrantRepository;
    private final UtilisateurRepository utilisateurRepository;

    public StageResponse creerStage(StageRequest request) {
        Stage stage = new Stage();
        stage.setTitre(request.getTitre());
        stage.setDescription(request.getDescription());
        stage.setService(request.getService());
        stage.setDateDebut(request.getDateDebut());
        stage.setDateFin(request.getDateFin());
        stage.setTypeStage(request.getTypeStage());
        stage.setNbPlaces(request.getNbPlaces());
        stage.setStatut(StatutStage.OUVERT);
        return toResponse(stageRepository.save(stage));
    }

    public Page<StageResponse> listerStages(StatutStage statut, TypeStage typeStage, String service, Pageable pageable) {
        boolean hasStatut  = statut != null;
        boolean hasType    = typeStage != null;
        boolean hasService = service != null && !service.isBlank();

        Page<Stage> result;

        if (hasStatut && hasType && hasService) {
            result = stageRepository.findByStatutAndTypeStageAndServiceContainingIgnoreCase(statut, typeStage, service, pageable);
        } else if (hasStatut && hasType) {
            result = stageRepository.findByStatutAndTypeStage(statut, typeStage, pageable);
        } else if (hasStatut && hasService) {
            result = stageRepository.findByStatutAndServiceContainingIgnoreCase(statut, service, pageable);
        } else if (hasType && hasService) {
            result = stageRepository.findByTypeStageAndServiceContainingIgnoreCase(typeStage, service, pageable);
        } else if (hasStatut) {
            result = stageRepository.findByStatut(statut, pageable);
        } else if (hasType) {
            result = stageRepository.findByTypeStage(typeStage, pageable);
        } else if (hasService) {
            result = stageRepository.findByServiceContainingIgnoreCase(service, pageable);
        } else {
            result = stageRepository.findAll(pageable);
        }

        return result.map(this::toResponse);
    }

    /**
     * Récupérer les stages affectés à l'encadrant authentifié
     */
    public Page<StageResponse> listerStagesEncadrant(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        var encadrant = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new BusinessException("Profil encadrant introuvable"));

        // Récupérer toutes les affectations de cet encadrant
        var affectations = affectationRepository.findByEncadrantId(encadrant.getId());

        // Extraire les stages uniques de ces affectations
        var stageIds = affectations.stream()
                .map(a -> a.getCandidature().getStage().getId())
                .distinct()
                .collect(Collectors.toList());

        if (stageIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Récupérer les stages
        List<Stage> stages = stageIds.stream()
                .map(id -> stageRepository.findById(id).orElse(null))
                .filter(s -> s != null)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                stages.stream().map(this::toResponse).toList(),
                pageable,
                stages.size()
        );
    }

    public StageResponse getStageById(Long id) {
        return toResponse(findById(id));
    }

    /**
     * CORRECTION : Protection contre modification avec candidatures acceptées
     */
    @Transactional
    public StageResponse modifierStage(Long id, StageRequest request) {
        Stage stage = findById(id);

        // Vérifier s'il y a des candidatures acceptées
        if (candidatureRepository.existsByStageIdAndStatut(id, StatutCandidature.ACCEPTEE)) {
            throw new BusinessException("Impossible de modifier un stage avec des candidatures acceptées");
        }

        stage.setTitre(request.getTitre());
        stage.setDescription(request.getDescription());
        stage.setService(request.getService());
        stage.setDateDebut(request.getDateDebut());
        stage.setDateFin(request.getDateFin());
        stage.setTypeStage(request.getTypeStage());
        stage.setNbPlaces(request.getNbPlaces());
        return toResponse(stageRepository.save(stage));
    }

    /**
     * CORRECTION : Protection contre suppression avec candidatures
     */
    @Transactional
    public void supprimerStage(Long id) {
        if (!stageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stage introuvable avec l'id : " + id);
        }

        // Vérifier s'il y a des candidatures acceptées
        if (candidatureRepository.existsByStageIdAndStatut(id, StatutCandidature.ACCEPTEE)) {
            throw new BusinessException("Impossible de supprimer un stage avec des candidatures acceptées");
        }

        stageRepository.deleteById(id);
    }

    @Transactional
    public StageResponse changerStatut(Long id, StatutStage nouveauStatut) {
        Stage stage = findById(id);
        stage.setStatut(nouveauStatut);
        return toResponse(stageRepository.save(stage));
    }

    /**
     * CORRECTION : Vérification stricte des places
     * Appelé par CandidatureService après acceptation d'une candidature.
     * Utilise une requête JPA native pour éviter les problèmes de mapping
     */
    @Transactional
    public void decrementerPlaces(Long stageId) {
        Stage stage = findById(stageId);
        
        if (stage.getNbPlaces() <= 0) {
            throw new BusinessException("Plus de places disponibles pour ce stage");
        }

        // Utiliser la méthode du repository qui fait une mise à jour SQL directe
        stageRepository.decrementerPlaces(stageId);
    }

    /**
     * Incrémenter les places si une candidature acceptée est refusée (nouvelle fonctionnalité)
     */
    @Transactional
    public void incrementerPlaces(Long stageId) {
        Stage stage = findById(stageId);
        
        // Utiliser la méthode du repository qui fait une mise à jour SQL directe
        stageRepository.incrementerPlaces(stageId);
    }

    private Stage findById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stage introuvable avec l'id : " + id));
    }

    private StageResponse toResponse(Stage stage) {
        return new StageResponse(
                stage.getId(),
                stage.getTitre(),
                stage.getDescription(),
                stage.getService(),
                stage.getDateDebut(),
                stage.getDateFin(),
                stage.getTypeStage(),
                stage.getStatut(),
                stage.getNbPlaces(),
                stage.getDateCreation()
        );
    }
}
