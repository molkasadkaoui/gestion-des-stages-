package tn.poste.gestionstages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Stagiaire;

import java.util.Optional;

public interface StagiaireRepository extends JpaRepository<Stagiaire, Long> {
    Optional<Stagiaire> findByUtilisateurId(Long utilisateurId);
}