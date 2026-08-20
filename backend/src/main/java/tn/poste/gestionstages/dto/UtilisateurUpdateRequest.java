package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.Role;

@Getter
@Setter
public class UtilisateurUpdateRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    private String telephone;

    private String service;

    private String etablissement;

    private String niveauEtude;

    private String filiere;

    private String poste;

    private Role role;

    private Boolean actif;

    private Boolean approuve;
}
