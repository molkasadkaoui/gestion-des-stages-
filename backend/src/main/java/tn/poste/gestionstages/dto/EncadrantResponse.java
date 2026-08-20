package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EncadrantResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String service;
    private String poste;
    private String telephone;
    private int nbStagiairesActuels; // charge actuelle
}
