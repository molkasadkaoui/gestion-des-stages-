package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EvaluationResponse {
    private Long id;
    private Long affectationId;
    private String stagiaireNom;
    private String encadrantNom;
    private BigDecimal note;
    private String commentaire;
    private LocalDateTime dateEvaluation;
}