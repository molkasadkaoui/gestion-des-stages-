package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Evaluation;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByAffectationId(Long affectationId);
}