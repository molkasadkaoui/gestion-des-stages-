package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AffectationResponse {
    private Long id;
    private Long candidatureId;
    private String stagiaireNom;
    private String stageTitre;
    private Long encadrantId;
    private String encadrantNom;
    private LocalDateTime dateAffectation;
}