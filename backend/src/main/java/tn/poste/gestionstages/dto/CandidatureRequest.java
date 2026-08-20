package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatureRequest {

    // stagiaireId supprimé - récupéré du token JWT dans le service
    // L'utilisateur authentifié est automatiquement identifié via SecurityContext

    @NotNull(message = "L'identifiant du stage est obligatoire")
    private Long stageId;

    private String cvUrl;

    private String lettreMotivation;
}