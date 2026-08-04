package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.AffectationRequest;
import tn.poste.gestionstages.dto.AffectationResponse;
import tn.poste.gestionstages.service.AffectationService;

import java.util.List;

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
    public ResponseEntity<List<AffectationResponse>> listerToutes() {
        return ResponseEntity.ok(affectationService.listerToutes());
    }

    @GetMapping("/encadrant/{encadrantId}")
    public ResponseEntity<List<AffectationResponse>> listerParEncadrant(@PathVariable Long encadrantId) {
        return ResponseEntity.ok(affectationService.listerParEncadrant(encadrantId));
    }
    @GetMapping("/stagiaire/{stagiaireId}")
    public ResponseEntity<List<AffectationResponse>> listerParStagiaire(@PathVariable Long stagiaireId) {
        return ResponseEntity.ok(affectationService.listerParStagiaire(stagiaireId));
    }
}