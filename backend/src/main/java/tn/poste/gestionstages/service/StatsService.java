package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.StatsResponse;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.enums.StatutRapport;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.CandidatureRepository;
import tn.poste.gestionstages.repository.RapportRepository;
import tn.poste.gestionstages.repository.StageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StageRepository       stageRepository;
    private final CandidatureRepository candidatureRepository;
    private final AffectationRepository affectationRepository;
    private final RapportRepository     rapportRepository;

    public StatsResponse getStats() {
        try {
            // Stages
            long totalStages   = stageRepository.count();
            long stagesOuverts = stageRepository.countByStatut(StatutStage.OUVERT);
            long stagesFermes  = stageRepository.countByStatut(StatutStage.FERME);

            // Candidatures
            long totalCandidatures = candidatureRepository.count();
            long enAttente         = candidatureRepository.countByStatut(StatutCandidature.EN_ATTENTE);
            long acceptees         = candidatureRepository.countByStatut(StatutCandidature.ACCEPTEE);
            long refusees          = candidatureRepository.countByStatut(StatutCandidature.REFUSEE);

            // Affectations
            long totalAffectations = affectationRepository.count();

            // Rapports - essayer de charger, si ça échoue mettre à 0
            long totalRapports  = 0;
            long rapportsValides = 0;
            try {
                totalRapports  = rapportRepository.count();
                rapportsValides = rapportRepository.countByStatut(StatutRapport.VALIDE);
            } catch (Exception e) {
                System.err.println("Avertissement: Impossible de charger les stats rapports : " + e.getMessage());
                // Garder les valeurs à 0
            }

            // Taux d'acceptation
            double tauxAcceptation = totalCandidatures > 0
                    ? BigDecimal.valueOf(acceptees * 100.0 / totalCandidatures)
                            .setScale(1, RoundingMode.HALF_UP)
                            .doubleValue()
                    : 0.0;

            // Note: Évaluations supprimées (système désactivé)
            long totalEvaluations = 0;
            double moyenneNotes = 0.0;

            return new StatsResponse(
                    totalStages, stagesOuverts, stagesFermes,
                    totalCandidatures, enAttente, acceptees, refusees,
                    totalAffectations, totalEvaluations,
                    totalRapports, rapportsValides,
                    tauxAcceptation, moyenneNotes
            );
        } catch (Exception e) {
            System.err.println("Erreur critique dans StatsService.getStats() : " + e.getMessage());
            e.printStackTrace();
            // Retourner des stats vides par défaut
            return new StatsResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0);
        }
    }
}
