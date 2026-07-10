package tn.poste.gestionstages.repository;

import tn.poste.gestionstages.entity.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByStagiaireId(Long stagiaireId);
    List<Candidature> findByStageId(Long stageId);
}