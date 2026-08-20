package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UtilisateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Boolean actif;
    private Boolean approuve;
    private LocalDateTime dateCreation;
    private LocalDateTime dateApprobation;
    private String telephone;
    private String service;
    private String etablissement;
    private String niveauEtude;
    private String filiere;
    private String poste;
}
