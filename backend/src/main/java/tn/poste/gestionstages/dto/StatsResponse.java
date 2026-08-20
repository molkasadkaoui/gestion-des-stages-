package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatsResponse {
    private long totalStages;
    private long stagesOuverts;
    private long stagesFermes;
    private long totalCandidatures;
    private long candidaturesEnAttente;
    private long candidaturesAcceptees;
    private long candidaturesRefusees;
    private long totalAffectations;
    private long totalEvaluations;
    private long totalRapports;
    private long rapportsValides;
    private double tauxAcceptation; // pourcentage
    private double moyenneGeneraleNotes; // moyenne de toutes les évaluations
}
