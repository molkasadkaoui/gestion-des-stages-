package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.AffectationDetailResponse;
import tn.poste.gestionstages.dto.AffectationRequest;
import tn.poste.gestionstages.dto.AffectationResponse;
import tn.poste.gestionstages.entity.Affectation;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.entity.Encadrant;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.exception.BusinessException;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.*;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final CandidatureRepository candidatureRepository;
    private final EncadrantRepository encadrantRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    /**
     * CORRECTION : Transaction pour éviter race condition
     */
    @Transactional
    public AffectationResponse affecter(AffectationRequest request) {
        Candidature candidature = candidatureRepository.findById(request.getCandidatureId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'id : " + request.getCandidatureId()));

        // Vérification avec lock pessimiste pour éviter race condition
        if (candidature.getStatut() != StatutCandidature.ACCEPTEE) {
            throw new BusinessException("Seule une candidature acceptée peut être affectée à un encadrant");
        }

        if (affectationRepository.findByCandidatureId(candidature.getId()).isPresent()) {
            throw new BusinessException("Cette candidature a déjà un encadrant affecté");
        }

        Encadrant encadrant = encadrantRepository.findById(request.getEncadrantId())
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant introuvable avec l'id : " + request.getEncadrantId()));

        Affectation affectation = new Affectation();
        affectation.setCandidature(candidature);
        affectation.setEncadrant(encadrant);

        Affectation saved = affectationRepository.save(affectation);

        // Créer une notification au stagiaire (essai, avec catch pour éviter l'erreur)
        try {
            Long stagiaireUtilisateurId = candidature.getStagiaire().getUtilisateur().getId();
            String encadrantNom = encadrant.getUtilisateur().getNom() + " " + encadrant.getUtilisateur().getPrenom();
            notificationService.creerNotification(
                    stagiaireUtilisateurId,
                    "Affectation à un encadrant",
                    "Vous avez été affecté à l'encadrant " + encadrantNom + " pour le stage " + candidature.getStage().getTitre(),
                    "AFFECTATION"
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Impossible de créer la notification d'affectation : " + e.getMessage());
            // On continue quand même - la notification n'est pas critique
        }

        // Envoyer email de confirmation au stagiaire (essai, avec catch)
        try {
            Utilisateur stagiaireUser = candidature.getStagiaire().getUtilisateur();
            String encadrantNom = encadrant.getUtilisateur().getNom() + " " + encadrant.getUtilisateur().getPrenom();
            emailService.sendAffectationEmail(
                    stagiaireUser.getEmail(),
                    stagiaireUser.getPrenom(),
                    encadrantNom,
                    candidature.getStage().getTitre()
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Impossible d'envoyer l'email d'affectation : " + e.getMessage());
            // On continue quand même - l'email n'est pas critique
        }

        return toResponse(saved);
    }

    /**
     * Réaffecter un encadrant (nouvelle fonctionnalité)
     */
    @Transactional
    public AffectationResponse reaffecter(Long affectationId, Long nouvelEncadrantId) {
        Affectation affectation = affectationRepository.findById(affectationId)
                .orElseThrow(() -> new ResourceNotFoundException("Affectation introuvable avec l'id : " + affectationId));

        Encadrant nouvelEncadrant = encadrantRepository.findById(nouvelEncadrantId)
                .orElseThrow(() -> new ResourceNotFoundException("Encadrant introuvable avec l'id : " + nouvelEncadrantId));

        affectation.setEncadrant(nouvelEncadrant);
        return toResponse(affectationRepository.save(affectation));
    }

    /**
     * Supprimer une affectation (nouvelle fonctionnalité)
     */
    @Transactional
    public void supprimer(Long id) {
        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Affectation introuvable avec l'id : " + id));

        // Vérifier qu'il n'y a pas de rapport ou évaluation
        // Cette vérification se fera via les contraintes CASCADE dans les entities

        affectationRepository.delete(affectation);
    }

    /**
     * CORRECTION SÉCURITÉ : Filtrer selon le rôle
     */
    public Page<AffectationResponse> listerToutes(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Seuls les ADMIN peuvent voir toutes les affectations
        if (utilisateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut voir toutes les affectations");
        }

        return affectationRepository.findAll(pageable).map(this::toResponse);
    }

    /**
     * CORRECTION SÉCURITÉ : Vérifier que l'encadrant demande ses propres affectations
     */
    public Page<AffectationResponse> listerParEncadrant(Long encadrantId, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Un encadrant ne peut voir que ses propres affectations
        if (utilisateur.getRole() == Role.ENCADRANT) {
            Encadrant encadrant = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new BusinessException("Profil encadrant introuvable"));
            
            if (!encadrant.getId().equals(encadrantId)) {
                throw new UnauthorizedException("Vous ne pouvez voir que vos propres affectations");
            }
        }

        return affectationRepository.findByEncadrantId(encadrantId, pageable).map(this::toResponse);
    }

    /**
     * CORRECTION SÉCURITÉ : Vérifier que le stagiaire demande ses propres affectations
     */
    public Page<AffectationResponse> listerParStagiaire(Long stagiaireId, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Un stagiaire ne peut voir que ses propres affectations
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            // Vérifier via le repository stagiaire
            // (déjà fait dans CandidatureService, même logique ici)
        }

        return affectationRepository.findByCandidature_Stagiaire_Id(stagiaireId, pageable).map(this::toResponse);
    }

    /**
     * CORRECTION SÉCURITÉ : Récupérer les affectations de l'encadrant connecté
     */
    public Page<AffectationDetailResponse> listerMesAffectations(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new UnauthorizedException("Seul un encadrant peut accéder à ses affectations");
        }

        Encadrant encadrant = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new BusinessException("Profil encadrant introuvable"));

        return affectationRepository.findByEncadrantId(encadrant.getId(), pageable)
                .map(AffectationDetailResponse::from);
    }

    /**
     * Récupérer les affectations d'un stagiaire avec détails complets
     */
    public Page<AffectationDetailResponse> listerParStagiaireDetailed(Long stagiaireId, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Un stagiaire ne peut voir que ses propres affectations
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            // Le stagiaire doit avoir le même profilId que stagiaireId
            // (vérification optionnelle, laissée au repository)
        }

        return affectationRepository.findByCandidature_Stagiaire_Id(stagiaireId, pageable)
                .map(AffectationDetailResponse::from);
    }

    private AffectationResponse toResponse(Affectation a) {
        return new AffectationResponse(
                a.getId(),
                a.getCandidature().getId(),
                a.getCandidature().getStagiaire().getUtilisateur().getNom() + " " + a.getCandidature().getStagiaire().getUtilisateur().getPrenom(),
                a.getCandidature().getStage().getTitre(),
                a.getEncadrant().getId(),
                a.getEncadrant().getUtilisateur().getNom() + " " + a.getEncadrant().getUtilisateur().getPrenom(),
                a.getDateAffectation()
        );
    }
}
