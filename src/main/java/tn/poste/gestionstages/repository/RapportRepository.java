package tn.poste.gestionstages.repository;

import tn.poste.gestionstages.entity.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByAffectationId(Long affectationId);
}