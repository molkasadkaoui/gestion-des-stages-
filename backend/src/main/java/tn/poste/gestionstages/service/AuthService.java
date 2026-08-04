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
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());
        utilisateur = utilisateurRepository.save(utilisateur);

        Long profilId = null;

        if (request.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = new Stagiaire();
            stagiaire.setUtilisateur(utilisateur);
            stagiaire.setEtablissement(request.getEtablissement());
            stagiaire.setNiveauEtude(request.getNiveauEtude());
            stagiaire.setFiliere(request.getFiliere());
            stagiaire.setTelephone(request.getTelephone());
            stagiaire = stagiaireRepository.save(stagiaire);
            profilId = stagiaire.getId();

        } else if (request.getRole() == Role.ENCADRANT) {
            Encadrant encadrant = new Encadrant();
            encadrant.setUtilisateur(utilisateur);
            encadrant.setService(request.getService());
            encadrant.setPoste(request.getPoste());
            encadrant.setTelephone(request.getTelephone());
            encadrant = encadrantRepository.save(encadrant);
            profilId = encadrant.getId();
        }

        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole().name(), utilisateur.getId());

        return new AuthResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                profilId,
                token
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

        Long profilId = null;
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            profilId = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                    .map(Stagiaire::getId)
                    .orElse(null);
        } else if (utilisateur.getRole() == Role.ENCADRANT) {
            profilId = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                    .map(Encadrant::getId)
                    .orElse(null);
        }

        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole().name(), utilisateur.getId());

        return new AuthResponse(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                profilId,
                token
        );
    }
}