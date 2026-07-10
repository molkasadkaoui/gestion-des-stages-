package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.enums.TypeStage;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class StageResponse {
    private Long id;
    private String titre;
    private String description;
    private String service;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private TypeStage typeStage;
    private StatutStage statut;
    private Integer nbPlaces;
    private LocalDateTime dateCreation;
}