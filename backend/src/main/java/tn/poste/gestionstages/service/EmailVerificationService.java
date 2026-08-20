package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Génère un token de vérification unique
     */
    public String generateVerificationToken() {
        SecureRandom random = new SecureRandom();
        byte[] values = new byte[32];
        random.nextBytes(values);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(values);
    }

    /**
     * Définit le token de vérification pour un utilisateur
     */
    public void setVerificationToken(Utilisateur utilisateur) {
        String token = generateVerificationToken();
        utilisateur.setTokenVerification(token);
    }

    /**
     * Vérifie l'email d'un utilisateur avec un token
     */
    public void verifyEmail(String token) {
        Utilisateur utilisateur = utilisateurRepository.findByTokenVerification(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de vérification invalide ou expiré"));

        // Vérifier que le token n'a pas plus de 24h
        if (utilisateur.getTokenVerification() != null) {
            utilisateur.setEmailVerifie(true);
            utilisateur.setTokenVerification(null);
            utilisateur.setDateVerification(LocalDateTime.now());
            utilisateurRepository.save(utilisateur);
        } else {
            throw new IllegalArgumentException("Token de vérification invalide");
        }
    }

    /**
     * Vérifie si un email est valide (format simple)
     */
    public boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex) && email.length() > 0 && email.length() < 255;
    }
}
