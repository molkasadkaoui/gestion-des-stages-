package tn.poste.gestionstages.repository;

import tn.poste.gestionstages.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByAffectationId(Long affectationId);
}