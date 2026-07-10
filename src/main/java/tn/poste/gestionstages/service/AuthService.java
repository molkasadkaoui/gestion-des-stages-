package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.AuthResponse;
import tn.poste.gestionstages.dto.LoginRequest;
import tn.poste.gestionstages.dto.RegisterRequest;
import tn.poste.gestionstages.entity.Encadrant;
import tn.poste.gestionstages.entity.Stagiaire;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.repository.EncadrantRepository;
import tn.poste.gestionstages.repository.StagiaireRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final StagiaireRepository stagiaireRepository;
    private final EncadrantRepository encadrantRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        // 1. Créer l'utilisateur de base
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());
        utilisateur = utilisateurRepository.save(utilisateur);

        // 2. Créer le profil spécifique selon le rôle
        if (request.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = new Stagiaire();
            stagiaire.setUtilisateur(utilisateur);
            stagiaire.setEtablissement(request.getEtablissement());
            stagiaire.setNiveauEtude(request.getNiveauEtude());
            stagiaire.setFiliere(request.getFiliere());
            stagiaire.setTelephone(request.getTelephone());
            stagiaireRepository.save(stagiaire);

        } else if (request.getRole() == Role.ENCADRANT) {
            Encadrant encadrant = new Encadrant();
            encadrant.setUtilisateur(utilisateur);
            encadrant.setService(request.getService());
            encadrant.setPoste(request.getPoste());
            encadrant.setTelephone(request.getTelephone());
            encadrantRepository.save(encadrant);
        }
        // Si ADMIN : rien de plus à créer, l'utilisateur de base suffit

        return new AuthResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        if (!utilisateur.getActif()) {
            throw new IllegalStateException("Ce compte a été désactivé.");
        }

        return new AuthResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }
}