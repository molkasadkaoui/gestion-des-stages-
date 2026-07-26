package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffectationRequest {

    @NotNull(message = "L'identifiant de la candidature est obligatoire")
    private Long candidatureId;

    @NotNull(message = "L'identifiant de l'encadrant est obligatoire")
    private Long encadrantId;
}