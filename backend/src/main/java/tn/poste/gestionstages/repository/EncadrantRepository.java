package tn.poste.gestionstages.repository;

import tn.poste.gestionstages.entity.Encadrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EncadrantRepository extends JpaRepository<Encadrant, Long> {
    Optional<Encadrant> findByUtilisateurId(Long utilisateurId);
}