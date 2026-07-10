package tn.poste.gestionstages.repository;

import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.enums.StatutStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findByStatut(StatutStage statut);
}