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
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @NotBlank(message = "Le service est obligatoire")
    @Size(max = 100, message = "Le service ne peut pas dépasser 100 caractères")
    private String service;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le type de stage est obligatoire")
    private TypeStage typeStage;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 place")
    @Max(value = 50, message = "Le nombre de places ne peut pas dépasser 50")
    private Integer nbPlaces = 1;

    @AssertTrue(message = "La date de début doit être avant la date de fin")
    public boolean isDateRangeValid() {
        if (dateDebut == null || dateFin == null) {
            return true; // Sera validé par @NotNull
        }
        return dateDebut.isBefore(dateFin);
    }
}