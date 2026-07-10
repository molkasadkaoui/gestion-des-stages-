package tn.poste.gestionstages.dto;

import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.Role;

@Getter
@Setter
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;

    // Champs optionnels selon le rôle
    private String etablissement;   // si STAGIAIRE
    private String niveauEtude;     // si STAGIAIRE
    private String filiere;         // si STAGIAIRE
    private String service;         // si ENCADRANT
    private String poste;           // si ENCADRANT
    private String telephone;
}