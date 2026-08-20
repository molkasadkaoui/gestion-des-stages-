package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.StageRequest;
import tn.poste.gestionstages.dto.StageResponse;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.enums.TypeStage;
import tn.poste.gestionstages.service.StageService;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @PostMapping
    public ResponseEntity<StageResponse> creerStage(@Valid @RequestBody StageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stageService.creerStage(request));
    }

    // Filtres optionnels avec pagination : ?statut=OUVERT&typeStage=PFE&service=Info&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<StageResponse>> listerStages(
            @RequestParam(required = false) StatutStage statut,
            @RequestParam(required = false) TypeStage typeStage,
            @RequestParam(required = false) String service,
            Pageable pageable) {
        return ResponseEntity.ok(stageService.listerStages(statut, typeStage, service, pageable));
    }

    @GetMapping("/mes-stages")
    public ResponseEntity<Page<StageResponse>> mesStages(Pageable pageable) {
        return ResponseEntity.ok(stageService.listerStagesEncadrant(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageResponse> getStageById(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.getStageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResponse> modifierStage(@PathVariable Long id,
                                                        @Valid @RequestBody StageRequest request) {
        return ResponseEntity.ok(stageService.modifierStage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerStage(@PathVariable Long id) {
        stageService.supprimerStage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/fermer")
    public ResponseEntity<StageResponse> fermerStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.changerStatut(id, StatutStage.FERME));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<StageResponse> annulerStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.changerStatut(id, StatutStage.ANNULE));
    }

    @PatchMapping("/{id}/rouvrir")
    public ResponseEntity<StageResponse> rouvrirStage(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.changerStatut(id, StatutStage.OUVERT));
    }
}
