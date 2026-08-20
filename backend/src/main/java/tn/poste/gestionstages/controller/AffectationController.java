package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.AffectationDetailResponse;
import tn.poste.gestionstages.dto.AffectationRequest;
import tn.poste.gestionstages.dto.AffectationResponse;
import tn.poste.gestionstages.service.AffectationService;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;

    @PostMapping
    public ResponseEntity<AffectationResponse> affecter(@Valid @RequestBody AffectationRequest request) {
        AffectationResponse response = affectationService.affecter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<AffectationResponse>> listerToutes(Pageable pageable) {
        return ResponseEntity.ok(affectationService.listerToutes(pageable));
    }

    @GetMapping("/encadrant/{encadrantId}")
    public ResponseEntity<Page<AffectationResponse>> listerParEncadrant(@PathVariable Long encadrantId, Pageable pageable) {
        return ResponseEntity.ok(affectationService.listerParEncadrant(encadrantId, pageable));
    }

    @GetMapping("/stagiaire/{stagiaireId}")
    public ResponseEntity<Page<AffectationDetailResponse>> listerParStagiaire(@PathVariable Long stagiaireId, Pageable pageable) {
        return ResponseEntity.ok(affectationService.listerParStagiaireDetailed(stagiaireId, pageable));
    }

    @PatchMapping("/{id}/reaffecter/{nouvelEncadrantId}")
    public ResponseEntity<AffectationResponse> reaffecter(@PathVariable Long id, @PathVariable Long nouvelEncadrantId) {
        return ResponseEntity.ok(affectationService.reaffecter(id, nouvelEncadrantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        affectationService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mes-affectations")
    public ResponseEntity<Page<AffectationDetailResponse>> listerMesAffectations(Pageable pageable) {
        return ResponseEntity.ok(affectationService.listerMesAffectations(pageable));
    }
}