package tn.poste.gestionstages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Rapport;
import tn.poste.gestionstages.enums.StatutRapport;

import java.util.Optional;

public interface RapportRepository extends JpaRepository<Rapport, Long> {

    /**
     * Trouver un rapport par affectation
     */
    Optional<Rapport> findByAffectationId(Long affectationId);

    /**
     * Lister tous les rapports d'un stagiaire
     */
    Page<Rapport> findByAffectation_Candidature_Stagiaire_Id(Long stagiaireId, Pageable pageable);

    /**
     * Lister tous les rapports pour un encadrant
     */
    Page<Rapport> findByAffectation_Encadrant_Id(Long encadrantId, Pageable pageable);

    /**
     * Lister par statut
     */
    Page<Rapport> findByStatut(StatutRapport statut, Pageable pageable);

    /**
     * Compter par statut
     */
    long countByStatut(StatutRapport statut);
}
