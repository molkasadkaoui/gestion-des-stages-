package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.NotificationResponse;
import tn.poste.gestionstages.entity.Notification;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.NotificationRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Créer une notification pour un utilisateur
     */
    @Transactional
    public void creerNotification(Long utilisateurId, String titre, String message, String type) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setType(type);
        notification.setLu(false);

        notificationRepository.save(notification);
    }

    /**
     * Récupérer les notifications d'un utilisateur authentifié
     */
    public Page<NotificationResponse> getMesNotifications(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        return notificationRepository
                .findByUtilisateurIdOrderByDateCreationDesc(utilisateur.getId(), pageable)
                .map(this::toResponse);
    }

    /**
     * Récupérer le nombre de notifications non lues
     */
    public long getNombreNonLues() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        return notificationRepository.countByUtilisateurIdAndLuFalse(utilisateur.getId());
    }

    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public void marquerCommeLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (!notification.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new UnauthorizedException("Vous ne pouvez marquer que vos propres notifications");
        }

        notificationRepository.markAsRead(notificationId);
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    @Transactional
    public void marquerToutCommeLu() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        notificationRepository.markAllAsRead(utilisateur.getId());
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitre(),
                n.getMessage(),
                n.getType(),
                n.getLu(),
                n.getDateCreation()
        );
    }
}
