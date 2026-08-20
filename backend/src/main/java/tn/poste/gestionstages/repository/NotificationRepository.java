package tn.poste.gestionstages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.poste.gestionstages.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Récupérer les notifications d'un utilisateur
     */
    Page<Notification> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId, Pageable pageable);

    /**
     * Récupérer les notifications non lues
     */
    List<Notification> findByUtilisateurIdAndLuFalseOrderByDateCreationDesc(Long utilisateurId);

    /**
     * Compter les notifications non lues
     */
    long countByUtilisateurIdAndLuFalse(Long utilisateurId);

    /**
     * Marquer les notifications comme lues
     */
    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.utilisateur.id = :utilisateurId")
    void markAllAsRead(@Param("utilisateurId") Long utilisateurId);

    /**
     * Marquer une notification comme lue
     */
    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
}
