package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.RapportRequest;
import tn.poste.gestionstages.dto.RapportResponse;
import tn.poste.gestionstages.entity.Affectation;
import tn.poste.gestionstages.entity.Rapport;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.enums.StatutRapport;
import tn.poste.gestionstages.exception.BusinessException;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.RapportRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;
    private final AffectationRepository affectationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    /**
     * Dépôt d'un rapport par le stagiaire
     */
    @Transactional
    public RapportResponse deposerRapport(RapportRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.STAGIAIRE) {
            throw new UnauthorizedException("Seul un stagiaire peut déposer un rapport");
        }

        Affectation affectation = affectationRepository.findById(request.getAffectationId())
                .orElseThrow(() -> new ResourceNotFoundException("Affectation introuvable"));

        // Vérifier que c'est bien l'affectation du stagiaire
        if (!affectation.getCandidature().getStagiaire().getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new UnauthorizedException("Vous ne pouvez déposer un rapport que pour vos propres affectations");
        }

        // Vérifier qu'il n'y a pas déjà un rapport
        if (rapportRepository.findByAffectationId(affectation.getId()).isPresent()) {
            throw new BusinessException("Un rapport existe déjà pour cette affectation");
        }

        Rapport rapport = new Rapport();
        rapport.setAffectation(affectation);
        rapport.setFichierUrl(request.getFichierUrl());
        rapport.setCommentaire(request.getCommentaire());
        rapport.setStatut(StatutRapport.DEPOSE);

        Rapport saved = rapportRepository.save(rapport);

        // Créer une notification pour l'encadrant
        Long encadrantUtilisateurId = affectation.getEncadrant().getUtilisateur().getId();
        String stagiaireNom = affectation.getCandidature().getStagiaire().getUtilisateur().getNom() + " " +
                affectation.getCandidature().getStagiaire().getUtilisateur().getPrenom();
        notificationService.creerNotification(
                encadrantUtilisateurId,
                "Nouveau rapport à valider",
                "Le stagiaire " + stagiaireNom + " a déposé un rapport pour le stage " + affectation.getCandidature().getStage().getTitre(),
                "RAPPORT"
        );

        return toResponse(saved);
    }

    /**
     * Valider un rapport (encadrant)
     */
    @Transactional
    public RapportResponse validerRapport(Long rapportId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new UnauthorizedException("Seul un encadrant peut valider un rapport");
        }

        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport introuvable"));

        // Vérifier que c'est bien l'encadrant du rapport
        if (!rapport.getAffectation().getEncadrant().getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new UnauthorizedException("Vous ne pouvez valider que vos propres rapports");
        }

        if (rapport.getStatut() != StatutRapport.DEPOSE) {
            throw new BusinessException("Seul un rapport déposé peut être validé");
        }

        rapport.setStatut(StatutRapport.VALIDE);
        rapport.setDateValidation(LocalDateTime.now());
        Rapport saved = rapportRepository.save(rapport);

        // Créer une notification et envoyer email (essai avec catch)
        try {
            Long stagiaireUtilisateurId = rapport.getAffectation().getCandidature().getStagiaire().getUtilisateur().getId();
            notificationService.creerNotification(
                    stagiaireUtilisateurId,
                    "Rapport validé",
                    "Votre rapport pour le stage " + rapport.getAffectation().getCandidature().getStage().getTitre() + " a été validé",
                    "RAPPORT"
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Notification de validation non créée : " + e.getMessage());
        }

        try {
            Utilisateur stagiaireUser = rapport.getAffectation().getCandidature().getStagiaire().getUtilisateur();
            emailService.sendRapportValidatedEmail(
                    stagiaireUser.getEmail(),
                    stagiaireUser.getPrenom(),
                    rapport.getAffectation().getCandidature().getStage().getTitre()
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Email de validation non envoyé : " + e.getMessage());
        }

        return toResponse(saved);
    }

    /**
     * Rejeter un rapport (encadrant)
     */
    @Transactional
    public RapportResponse rejeterRapport(Long rapportId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new UnauthorizedException("Seul un encadrant peut rejeter un rapport");
        }

        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport introuvable"));

        // Vérifier que c'est bien l'encadrant du rapport
        if (!rapport.getAffectation().getEncadrant().getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new UnauthorizedException("Vous ne pouvez rejeter que vos propres rapports");
        }

        if (rapport.getStatut() != StatutRapport.DEPOSE) {
            throw new BusinessException("Seul un rapport déposé peut être rejeté");
        }

        rapport.setStatut(StatutRapport.REJETE);
        Rapport saved = rapportRepository.save(rapport);

        // Créer une notification et envoyer email (essai avec catch)
        try {
            Long stagiaireUtilisateurId = rapport.getAffectation().getCandidature().getStagiaire().getUtilisateur().getId();
            notificationService.creerNotification(
                    stagiaireUtilisateurId,
                    "Rapport rejeté",
                    "Votre rapport pour le stage " + rapport.getAffectation().getCandidature().getStage().getTitre() + " a été rejeté. Veuillez le revoir et le resoumettve",
                    "RAPPORT"
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Notification de rejet non créée : " + e.getMessage());
        }

        try {
            Utilisateur stagiaireUser = rapport.getAffectation().getCandidature().getStagiaire().getUtilisateur();
            emailService.sendRapportRejectedEmail(
                    stagiaireUser.getEmail(),
                    stagiaireUser.getPrenom(),
                    rapport.getAffectation().getCandidature().getStage().getTitre()
            );
        } catch (Exception e) {
            System.err.println("Avertissement: Email de rejet non envoyé : " + e.getMessage());
        }

        return toResponse(saved);
    }

    /**
     * Récupérer les rapports du stagiaire
     */
    public Page<RapportResponse> meseRapports(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.STAGIAIRE) {
            throw new UnauthorizedException("Seul un stagiaire peut accéder à ses rapports");
        }

        return rapportRepository
                .findByAffectation_Candidature_Stagiaire_Id(utilisateur.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * Récupérer les rapports pour un encadrant
     */
    public Page<RapportResponse> rapportsPourEncadrant(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new UnauthorizedException("Seul un encadrant peut accéder à ses rapports");
        }

        return rapportRepository
                .findByAffectation_Encadrant_Id(utilisateur.getId(), pageable)
                .map(this::toResponse);
    }

    private RapportResponse toResponse(Rapport r) {
        return new RapportResponse(
                r.getId(),
                r.getAffectation().getId(),
                r.getAffectation().getCandidature().getStagiaire().getUtilisateur().getNom() + " " +
                        r.getAffectation().getCandidature().getStagiaire().getUtilisateur().getPrenom(),
                r.getAffectation().getCandidature().getStage().getTitre(),
                r.getFichierUrl(),
                r.getCommentaire(),
                r.getStatut(),
                r.getDateSOumission(),
                r.getDateValidation()
        );
    }
}
