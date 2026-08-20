package tn.poste.gestionstages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.enums.StatutCandidature;

import java.util.List;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    boolean existsByStagiaireIdAndStageId(Long stagiaireId, Long stageId);
    List<Candidature> findByStagiaireId(Long stagiaireId);
    List<Candidature> findByStageId(Long stageId);
    Page<Candidature> findByStagiaireId(Long stagiaireId, Pageable pageable);
    Page<Candidature> findByStageId(Long stageId, Pageable pageable);
    long countByStatut(StatutCandidature statut);
    long countByStageIdAndStatut(Long stageId, StatutCandidature statut);
    boolean existsByStageIdAndStatut(Long stageId, StatutCandidature statut);
}
