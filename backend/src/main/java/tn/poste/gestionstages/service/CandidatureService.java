package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.CandidatureRequest;
import tn.poste.gestionstages.dto.CandidatureResponse;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.entity.Stagiaire;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.exception.BusinessException;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.CandidatureRepository;
import tn.poste.gestionstages.repository.StageRepository;
import tn.poste.gestionstages.repository.StagiaireRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final StagiaireRepository stagiaireRepository;
    private final StageRepository stageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final StageService stageService;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    /**
     * CORRECTION SÉCURITÉ : Extraire l'utilisateur du SecurityContext
     */
    @Transactional
    public CandidatureResponse postuler(CandidatureRequest request) {
        // 1. Récupérer l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // 2. Vérifier que c'est bien un stagiaire
        if (utilisateur.getRole() != Role.STAGIAIRE) {
            throw new UnauthorizedException("Seul un stagiaire peut postuler à un stage");
        }

        // 3. Récupérer le profil stagiaire de l'utilisateur
        Stagiaire stagiaire = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new BusinessException("Profil stagiaire introuvable"));

        // 4. Valider le stage
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new ResourceNotFoundException("Stage introuvable avec l'id : " + request.getStageId()));

        // 5. Vérifications métier
        if (stage.getStatut() != StatutStage.OUVERT) {
            throw new BusinessException("Ce stage n'accepte plus de candidatures (statut : " + stage.getStatut() + ")");
        }

        // Vérifier que le stage n'a pas déjà commencé
        if (stage.getDateDebut().isBefore(LocalDate.now())) {
            throw new BusinessException("Impossible de postuler à un stage déjà commencé");
        }

        // Vérifier qu'il reste des places
        if (stage.getNbPlaces() <= 0) {
            throw new BusinessException("Plus de places disponibles pour ce stage");
        }

        if (candidatureRepository.existsByStagiaireIdAndStageId(stagiaire.getId(), stage.getId())) {
            throw new BusinessException("Vous avez déjà postulé à ce stage");
        }

        // 6. Créer la candidature
        Candidature candidature = new Candidature();
        candidature.setStagiaire(stagiaire);
        candidature.setStage(stage);
        candidature.setCvUrl(request.getCvUrl());
        candidature.setLettreMotivation(request.getLettreMotivation());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);

        return toResponse(candidatureRepository.save(candidature));
    }

    /**
     * CORRECTION SÉCURITÉ : Filtrer selon le rôle
     */
    public Page<CandidatureResponse> listerToutes(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Seuls les ADMIN voient toutes les candidatures
        if (utilisateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut voir toutes les candidatures");
        }

        return candidatureRepository.findAll(pageable).map(this::toResponse);
    }

    /**
     * CORRECTION SÉCURITÉ : Vérifier que le stagiaire demande ses propres candidatures
     */
    public Page<CandidatureResponse> listerParStagiaire(Long stagiaireId, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Un stagiaire ne peut voir que ses propres candidatures
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new BusinessException("Profil stagiaire introuvable"));
            
            if (!stagiaire.getId().equals(stagiaireId)) {
                throw new UnauthorizedException("Vous ne pouvez voir que vos propres candidatures");
            }
        }

        return candidatureRepository.findByStagiaireId(stagiaireId, pageable).map(this::toResponse);
    }

    public Page<CandidatureResponse> listerParStage(Long stageId, Pageable pageable) {
        return candidatureRepository.findByStageId(stageId, pageable).map(this::toResponse);
    }

    /**
     * Annuler une candidature (nouvelle fonctionnalité)
     */
    @Transactional
    public void annuler(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        Candidature candidature = findById(id);

        // Vérifier que c'est bien la candidature du stagiaire
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new BusinessException("Profil stagiaire introuvable"));
            
            if (!candidature.getStagiaire().getId().equals(stagiaire.getId())) {
                throw new UnauthorizedException("Vous ne pouvez annuler que vos propres candidatures");
            }
        }

        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new BusinessException("Seule une candidature en attente peut être annulée");
        }

        candidatureRepository.delete(candidature);
    }

    @Transactional
    public CandidatureResponse accepter(Long id) {
        Candidature candidature = findById(id);

        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new BusinessException("Seule une candidature en attente peut être acceptée");
        }

        // Vérifier qu'il reste des places
        Stage stage = candidature.getStage();
        long candidaturesAcceptees = candidatureRepository.countByStageIdAndStatut(stage.getId(), StatutCandidature.ACCEPTEE);
        
        if (candidaturesAcceptees >= stage.getNbPlaces()) {
            throw new BusinessException("Plus de places disponibles pour ce stage");
        }

        candidature.setStatut(StatutCandidature.ACCEPTEE);
        Candidature saved = candidatureRepository.saveAndFlush(candidature);
        
        // Décrémenter les places du stage
        stageService.decrementerPlaces(candidature.getStage().getId());
        
        // Envoyer email et notification (essai avec catch pour éviter les erreurs)
        try {
            Utilisateur stagiaireUser = candidature.getStagiaire().getUtilisateur();
            emailService.sendCandidatureAcceptedEmail(
                    stagiaireUser.getEmail(),
                    stagiaireUser.getPrenom(),
                    stage.getTitre()
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Email d'acceptation non envoyé : " + e.getMessage());
        }

        try {
            notificationService.creerNotification(
                    candidature.getStagiaire().getUtilisateur().getId(),
                    "Candidature acceptée",
                    "Votre candidature au stage \"" + stage.getTitre() + "\" a été acceptée",
                    "CANDIDATURE"
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Notification d'acceptation non créée : " + e.getMessage());
        }
        
        return toResponse(saved);
    }

    @Transactional
    public CandidatureResponse refuser(Long id) {
        Candidature candidature = findById(id);

        if (candidature.getStatut() != StatutCandidature.EN_ATTENTE) {
            throw new BusinessException("Seule une candidature en attente peut être refusée");
        }

        candidature.setStatut(StatutCandidature.REFUSEE);
        Candidature saved = candidatureRepository.save(candidature);
        
        // Envoyer email et notification (essai avec catch)
        try {
            Utilisateur stagiaireUser = candidature.getStagiaire().getUtilisateur();
            emailService.sendCandidatureRejectedEmail(
                    stagiaireUser.getEmail(),
                    stagiaireUser.getPrenom(),
                    candidature.getStage().getTitre()
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Email de rejet non envoyé : " + e.getMessage());
        }
        
        try {
            notificationService.creerNotification(
                    candidature.getStagiaire().getUtilisateur().getId(),
                    "Candidature refusée",
                    "Votre candidature au stage \"" + candidature.getStage().getTitre() + "\" a été refusée",
                    "CANDIDATURE"
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Notification de rejet non créée : " + e.getMessage());
        }
        
        return toResponse(saved);
    }

    private Candidature findById(Long id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable avec l'id : " + id));
    }

    private CandidatureResponse toResponse(Candidature c) {
        return new CandidatureResponse(
                c.getId(),
                c.getStagiaire().getId(),
                c.getStagiaire().getUtilisateur().getNom() + " " + c.getStagiaire().getUtilisateur().getPrenom(),
                c.getStage().getId(),
                c.getStage().getTitre(),
                c.getDateCandidature(),
                c.getStatut(),
                c.getCvUrl(),
                c.getLettreMotivation()
        );
    }

    /**
     * Télécharger le CV d'une candidature
     */
    public ResponseEntity<Resource> downloadCandidatureCV(Long candidatureId) {
        Candidature candidature = findById(candidatureId);

        if (candidature.getCvUrl() == null || candidature.getCvUrl().isEmpty()) {
            throw new ResourceNotFoundException("Aucun CV associé à cette candidature");
        }

        // Extraire le nom du fichier de l'URL
        String fileName = extractFileNameFromUrl(candidature.getCvUrl());
        
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            // Déterminer le type MIME
            String contentType = "application/octet-stream";
            if (fileName.endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            } else if (fileName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (fileName.endsWith(".doc")) {
                contentType = "application/msword";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"CV\"")
                    .body(resource);
        } catch (Exception ex) {
            throw new BusinessException("Impossible de charger le fichier CV");
        }
    }

    /**
     * Extraire le nom du fichier de l'URL
     */
    private String extractFileNameFromUrl(String cvUrl) {
        // Si c'est une URL complète avec /api/files/download/, extraire le nom du fichier
        if (cvUrl.contains("/api/files/download/")) {
            return cvUrl.substring(cvUrl.lastIndexOf("/") + 1);
        }
        // Sinon, supposer que c'est déjà le nom du fichier
        return cvUrl;
    }
}
