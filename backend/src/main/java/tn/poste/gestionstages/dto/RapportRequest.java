package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RapportRequest {

    @NotNull(message = "L'identifiant de l'affectation est obligatoire")
    private Long affectationId;

    @NotBlank(message = "L'URL du fichier est obligatoire")
    private String fichierUrl;

    private String commentaire;
}