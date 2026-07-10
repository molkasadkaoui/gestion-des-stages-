package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.StageRequest;
import tn.poste.gestionstages.dto.StageResponse;
import tn.poste.gestionstages.service.StageService;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @PostMapping
    public ResponseEntity<StageResponse> creerStage(@Valid @RequestBody StageRequest request) {
        StageResponse response = stageService.creerStage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StageResponse>> listerStages() {
        return ResponseEntity.ok(stageService.listerStages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageResponse> getStageById(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.getStageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageResponse> modifierStage(@PathVariable Long id, @Valid @RequestBody StageRequest request) {
        return ResponseEntity.ok(stageService.modifierStage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerStage(@PathVariable Long id) {
        stageService.supprimerStage(id);
        return ResponseEntity.noContent().build();
    }
}