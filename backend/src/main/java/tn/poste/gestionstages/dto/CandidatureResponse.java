package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.StatutCandidature;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CandidatureResponse {
    private Long id;
    private Long stagiaireId;
    private String stagiaireNom;
    private Long stageId;
    private String stageTitre;
    private LocalDateTime dateCandidature;
    private StatutCandidature statut;
    private String cvUrl;
    private String lettreMotivation;
}