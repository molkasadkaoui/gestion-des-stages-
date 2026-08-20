package tn.poste.gestionstages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.enums.TypeStage;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {

    long countByStatut(StatutStage statut);

    // Filtres avec pagination
    Page<Stage> findByStatut(StatutStage statut, Pageable pageable);
    Page<Stage> findByTypeStage(TypeStage typeStage, Pageable pageable);
    Page<Stage> findByStatutAndTypeStage(StatutStage statut, TypeStage typeStage, Pageable pageable);
    Page<Stage> findByServiceContainingIgnoreCase(String service, Pageable pageable);
    Page<Stage> findByStatutAndServiceContainingIgnoreCase(StatutStage statut, String service, Pageable pageable);
    Page<Stage> findByTypeStageAndServiceContainingIgnoreCase(TypeStage typeStage, String service, Pageable pageable);
    Page<Stage> findByStatutAndTypeStageAndServiceContainingIgnoreCase(StatutStage statut, TypeStage typeStage, String service, Pageable pageable);

    /**
     * Décrémenter les places d'un stage directement en SQL
     */
    @Modifying
    @Query("UPDATE Stage s SET s.nbPlaces = s.nbPlaces - 1, s.statut = CASE WHEN s.nbPlaces - 1 <= 0 THEN 'FERME' ELSE s.statut END WHERE s.id = :stageId")
    void decrementerPlaces(@Param("stageId") Long stageId);

    /**
     * Incrémenter les places d'un stage directement en SQL
     */
    @Modifying
    @Query("UPDATE Stage s SET s.nbPlaces = s.nbPlaces + 1, s.statut = CASE WHEN s.statut = 'FERME' THEN 'OUVERT' ELSE s.statut END WHERE s.id = :stageId")
    void incrementerPlaces(@Param("stageId") Long stageId);
}
