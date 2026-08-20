package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.poste.gestionstages.dto.ChangePasswordRequest;
import tn.poste.gestionstages.dto.UtilisateurResponse;
import tn.poste.gestionstages.dto.UtilisateurUpdateRequest;
import tn.poste.gestionstages.entity.Encadrant;
import tn.poste.gestionstages.entity.Stagiaire;
import tn.poste.gestionstages.entity.Utilisateur;
import tn.poste.gestionstages.enums.Role;
import tn.poste.gestionstages.exception.BusinessException;
import tn.poste.gestionstages.exception.ResourceNotFoundException;
import tn.poste.gestionstages.exception.UnauthorizedException;
import tn.poste.gestionstages.repository.EncadrantRepository;
import tn.poste.gestionstages.repository.StagiaireRepository;
import tn.poste.gestionstages.repository.UtilisateurRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final StagiaireRepository stagiaireRepository;
    private final EncadrantRepository encadrantRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer tous les utilisateurs (ADMIN seulement)
     */
    public Page<UtilisateurResponse> listerUtilisateurs(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut voir tous les utilisateurs");
        }

        return utilisateurRepository.findAll(pageable).map(this::toResponse);
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public UtilisateurResponse obtenirUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));
        return toResponse(utilisateur);
    }

    /**
     * Récupérer le profil de l'utilisateur connecté
     */
    public UtilisateurResponse obtenirMonProfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));
        return toResponse(utilisateur);
    }

    /**
     * Mettre à jour le profil de l'utilisateur connecté
     */
    @Transactional
    public UtilisateurResponse mettreAJourMonProfil(UtilisateurUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Vérifier que l'email n'existe pas ailleurs
        if (!utilisateur.getEmail().equals(request.getEmail())) {
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("Cet email est déjà utilisé");
            }
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());

        // Mettre à jour les détails selon le rôle
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new BusinessException("Profil stagiaire introuvable"));
            if (request.getTelephone() != null) stagiaire.setTelephone(request.getTelephone());
            if (request.getEtablissement() != null) stagiaire.setEtablissement(request.getEtablissement());
            if (request.getNiveauEtude() != null) stagiaire.setNiveauEtude(request.getNiveauEtude());
            if (request.getFiliere() != null) stagiaire.setFiliere(request.getFiliere());
            stagiaireRepository.save(stagiaire);
        } else if (utilisateur.getRole() == Role.ENCADRANT) {
            Encadrant encadrant = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new BusinessException("Profil encadrant introuvable"));
            if (request.getTelephone() != null) encadrant.setTelephone(request.getTelephone());
            if (request.getService() != null) encadrant.setService(request.getService());
            if (request.getPoste() != null) encadrant.setPoste(request.getPoste());
            encadrantRepository.save(encadrant);
        }

        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    /**
     * Mettre à jour un utilisateur (ADMIN seulement)
     */
    @Transactional
    public UtilisateurResponse mettreAJourUtilisateur(Long id, UtilisateurUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut mettre à jour les utilisateurs");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        // Vérifier que l'email n'existe pas ailleurs
        if (!utilisateur.getEmail().equals(request.getEmail())) {
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("Cet email est déjà utilisé");
            }
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        
        if (request.getRole() != null) {
            utilisateur.setRole(request.getRole());
        }
        
        if (request.getActif() != null) {
            utilisateur.setActif(request.getActif());
        }

        // Mettre à jour les détails selon le rôle
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            Stagiaire stagiaire = stagiaireRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseGet(() -> {
                        Stagiaire newStagiaire = new Stagiaire();
                        newStagiaire.setUtilisateur(utilisateur);
                        return newStagiaire;
                    });
            if (request.getTelephone() != null) stagiaire.setTelephone(request.getTelephone());
            if (request.getEtablissement() != null) stagiaire.setEtablissement(request.getEtablissement());
            if (request.getNiveauEtude() != null) stagiaire.setNiveauEtude(request.getNiveauEtude());
            if (request.getFiliere() != null) stagiaire.setFiliere(request.getFiliere());
            stagiaireRepository.save(stagiaire);
        } else if (utilisateur.getRole() == Role.ENCADRANT) {
            Encadrant encadrant = encadrantRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseGet(() -> {
                        Encadrant newEncadrant = new Encadrant();
                        newEncadrant.setUtilisateur(utilisateur);
                        return newEncadrant;
                    });
            if (request.getTelephone() != null) encadrant.setTelephone(request.getTelephone());
            if (request.getService() != null) encadrant.setService(request.getService());
            if (request.getPoste() != null) encadrant.setPoste(request.getPoste());
            encadrantRepository.save(encadrant);
        }

        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    /**
     * Changer le mot de passe
     */
    @Transactional
    public void changerMotDePasse(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BusinessException("L'ancien mot de passe est incorrect");
        }

        // Vérifier que les nouveaux mots de passe correspondent
        if (!request.getNouveauMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new BusinessException("Les nouveaux mots de passe ne correspondent pas");
        }

        // Vérifier que le nouveau mot de passe n'est pas le même que l'ancien
        if (request.getNouveauMotDePasse().equals(request.getAncienMotDePasse())) {
            throw new BusinessException("Le nouveau mot de passe ne peut pas être identique à l'ancien");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprimer un utilisateur (ADMIN seulement)
     */
    @Transactional
    public void supprimerUtilisateur(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut supprimer les utilisateurs");
        }

        if (admin.getId().equals(id)) {
            throw new BusinessException("Vous ne pouvez pas supprimer votre propre compte");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        // Supprimer les profils associés
        if (utilisateur.getRole() == Role.STAGIAIRE) {
            stagiaireRepository.deleteByUtilisateurId(utilisateur.getId());
        } else if (utilisateur.getRole() == Role.ENCADRANT) {
            encadrantRepository.deleteByUtilisateurId(utilisateur.getId());
        }

        utilisateurRepository.delete(utilisateur);
    }

    /**
     * Désactiver un utilisateur (soft delete - ADMIN seulement)
     */
    @Transactional
    public UtilisateurResponse desactiverUtilisateur(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut désactiver les utilisateurs");
        }

        if (admin.getId().equals(id)) {
            throw new BusinessException("Vous ne pouvez pas désactiver votre propre compte");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    /**
     * Activer un utilisateur (ADMIN seulement)
     */
    @Transactional
    public UtilisateurResponse activerUtilisateur(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut activer les utilisateurs");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    /**
     * Rechercher utilisateurs par rôle
     */
    public Page<UtilisateurResponse> listerParRole(Role role, Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (utilisateur.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut voir les utilisateurs par rôle");
        }

        return utilisateurRepository.findByRole(role, pageable).map(this::toResponse);
    }

    /**
     * Approuver un encadrant (ADMIN seulement)
     */
    @Transactional
    public UtilisateurResponse appouverEncadrant(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut approuver les encadrants");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new BusinessException("Seul un encadrant peut être approuvé");
        }

        utilisateur.setApprouve(true);
        utilisateur.setDateApprobation(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    /**
     * Rejeter un encadrant (ADMIN seulement) - désactiver sans approuver
     */
    @Transactional
    public UtilisateurResponse rejeterEncadrant(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur admin = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non authentifié"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seul un administrateur peut rejeter les encadrants");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));

        if (utilisateur.getRole() != Role.ENCADRANT) {
            throw new BusinessException("Seul un encadrant peut être rejeté");
        }

        utilisateur.setApprouve(false);
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
        return toResponse(utilisateur);
    }

    private UtilisateurResponse toResponse(Utilisateur u) {
        UtilisateurResponse response = UtilisateurResponse.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .role(u.getRole())
                .actif(u.getActif())
                .approuve(u.getApprouve())
                .dateCreation(u.getDateCreation())
                .dateApprobation(u.getDateApprobation())
                .build();

        // Ajouter les détails selon le rôle
        if (u.getRole() == Role.STAGIAIRE) {
            stagiaireRepository.findByUtilisateurId(u.getId()).ifPresent(s -> {
                response.setTelephone(s.getTelephone());
                response.setEtablissement(s.getEtablissement());
                response.setNiveauEtude(s.getNiveauEtude());
                response.setFiliere(s.getFiliere());
            });
        } else if (u.getRole() == Role.ENCADRANT) {
            encadrantRepository.findByUtilisateurId(u.getId()).ifPresent(e -> {
                response.setTelephone(e.getTelephone());
                response.setService(e.getService());
                response.setPoste(e.getPoste());
            });
        }

        return response;
    }
}
