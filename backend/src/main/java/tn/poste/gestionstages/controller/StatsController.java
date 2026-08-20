package tn.poste.gestionstages.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.StatsResponse;
import tn.poste.gestionstages.service.StatsService;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public ResponseEntity<StatsResponse> getStats() {
        try {
            return ResponseEntity.ok(statsService.getStats());
        } catch (Exception e) {
            // Si les stats échouent, retourner des stats vides
            // C'est juste du dashboard, pas critique
            return ResponseEntity.ok(new StatsResponse(
                    0, 0, 0,  // stages
                    0, 0, 0, 0,  // candidatures
                    0, 0,  // affectations, évaluations
                    0, 0,  // rapports
                    0.0, 0.0  // taux, moyenne
            ));
        }
    }
}
