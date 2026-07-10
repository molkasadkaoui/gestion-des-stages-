package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.enums.TypeStage;

import java.time.LocalDate;

@Getter
@Setter
public class StageRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotBlank(message = "Le service est obligatoire")
    private String service;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le type de stage est obligatoire")
    private TypeStage typeStage;

    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    private Integer nbPlaces = 1;
}