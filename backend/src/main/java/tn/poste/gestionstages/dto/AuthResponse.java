package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.Role;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
    private Long profilId;
}