package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Rapport;

import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByAffectationId(Long affectationId);
}