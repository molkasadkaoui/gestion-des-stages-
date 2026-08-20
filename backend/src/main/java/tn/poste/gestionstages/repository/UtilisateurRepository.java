package tn.poste.gestionstages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Utilisateur> findByRole(Role role, Pageable pageable);
    Optional<Utilisateur> findByTokenVerification(String tokenVerification);
}