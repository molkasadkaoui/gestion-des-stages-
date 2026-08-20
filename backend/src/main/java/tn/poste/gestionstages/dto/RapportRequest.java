package tn.poste.gestionstages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RapportRequest {
    @NotNull(message = "L'ID de l'affectation est requis")
    private Long affectationId;

    @NotBlank(message = "L'URL du fichier est requise")
    private String fichierUrl;

    private String commentaire;
}
