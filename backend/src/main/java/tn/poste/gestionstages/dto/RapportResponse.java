package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.StatutRapport;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class RapportResponse {
    private Long id;
    private Long affectationId;
    private String stagiaireNom;
    private String stageTitre;
    private String fichierUrl;
    private String commentaire;
    private StatutRapport statut;
    private LocalDateTime datesoumission;
    private LocalDateTime dateValidation;
}
