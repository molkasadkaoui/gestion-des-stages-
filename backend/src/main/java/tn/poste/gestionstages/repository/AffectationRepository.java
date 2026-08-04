package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Affectation;

import java.util.List;
import java.util.Optional;

public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    Optional<Affectation> findByCandidatureId(Long candidatureId);
    List<Affectation> findByEncadrantId(Long encadrantId);
    List<Affectation> findByCandidature_Stagiaire_Id(Long stagiaireId);
}