package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.ChangePasswordRequest;
import tn.poste.gestionstages.dto.UtilisateurResponse;
import tn.poste.gestionstages.dto.UtilisateurUpdateRequest;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.service.UtilisateurService;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Lister tous les utilisateurs (ADMIN seulement)
     */
    @GetMapping
    public ResponseEntity<Page<UtilisateurResponse>> listerUtilisateurs(Pageable pageable) {
        return ResponseEntity.ok(utilisateurService.listerUtilisateurs(pageable));
    }

    /**
     * Récupérer mon profil
     */
    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponse> obtenirMonProfil() {
        return ResponseEntity.ok(utilisateurService.obtenirMonProfil());
    }

    /**
     * Mettre à jour mon profil
     */
    @PutMapping("/me")
    public ResponseEntity<UtilisateurResponse> mettreAJourMonProfil(
            @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.mettreAJourMonProfil(request));
    }

    /**
     * Changer mon mot de passe
     */
    @PostMapping("/me/changer-mot-de-passe")
    public ResponseEntity<Void> changerMotDePasse(@Valid @RequestBody ChangePasswordRequest request) {
        utilisateurService.changerMotDePasse(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupérer un utilisateur par ID (ADMIN seulement)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> obtenirUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.obtenirUtilisateur(id));
    }

    /**
     * Mettre à jour un utilisateur (ADMIN seulement)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> mettreAJourUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurUpdateRequest request) {
        return ResponseEntity.ok(utilisateurService.mettreAJourUtilisateur(id, request));
    }

    /**
     * Supprimer un utilisateur (ADMIN seulement)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Désactiver un utilisateur (ADMIN seulement)
     */
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<UtilisateurResponse> desactiverUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.desactiverUtilisateur(id));
    }

    /**
     * Activer un utilisateur (ADMIN seulement)
     */
    @PatchMapping("/{id}/activer")
    public ResponseEntity<UtilisateurResponse> activerUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.activerUtilisateur(id));
    }

    /**
     * Lister les utilisateurs par rôle (ADMIN seulement)
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UtilisateurResponse>> listerParRole(
            @PathVariable Role role,
            Pageable pageable) {
        return ResponseEntity.ok(utilisateurService.listerParRole(role, pageable));
    }

    /**
     * Approuver un encadrant (ADMIN seulement)
     */
    @PatchMapping("/{id}/approuver")
    public ResponseEntity<UtilisateurResponse> appouverEncadrant(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.appouverEncadrant(id));
    }

    /**
     * Rejeter un encadrant (ADMIN seulement)
     */
    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<UtilisateurResponse> rejeterEncadrant(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.rejeterEncadrant(id));
    }
}
