package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatureRequest {

    @NotNull(message = "L'identifiant du stagiaire est obligatoire")
    private Long stagiaireId;

    @NotNull(message = "L'identifiant du stage est obligatoire")
    private Long stageId;

    private String cvUrl;

    private String lettreMotivation;
}