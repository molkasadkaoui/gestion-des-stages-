package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Candidature;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    boolean existsByStagiaireIdAndStageId(Long stagiaireId, Long stageId);
    List<Candidature> findByStagiaireId(Long stagiaireId);
    List<Candidature> findByStageId(Long stageId);
}