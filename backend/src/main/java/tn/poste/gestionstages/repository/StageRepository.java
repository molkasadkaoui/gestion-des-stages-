package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Stage;

public interface StageRepository extends JpaRepository<Stage, Long> {
}