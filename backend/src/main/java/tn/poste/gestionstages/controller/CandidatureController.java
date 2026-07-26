package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.CandidatureRequest;
import tn.poste.gestionstages.dto.CandidatureResponse;
import tn.poste.gestionstages.service.CandidatureService;

import java.util.List;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;

    @PostMapping
    public ResponseEntity<CandidatureResponse> postuler(@Valid @RequestBody CandidatureRequest request) {
        CandidatureResponse response = candidatureService.postuler(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CandidatureResponse>> listerToutes() {
        return ResponseEntity.ok(candidatureService.listerToutes());
    }

    @GetMapping("/stagiaire/{stagiaireId}")
    public ResponseEntity<List<CandidatureResponse>> listerParStagiaire(@PathVariable Long stagiaireId) {
        return ResponseEntity.ok(candidatureService.listerParStagiaire(stagiaireId));
    }

    @GetMapping("/stage/{stageId}")
    public ResponseEntity<List<CandidatureResponse>> listerParStage(@PathVariable Long stageId) {
        return ResponseEntity.ok(candidatureService.listerParStage(stageId));
    }

    @PatchMapping("/{id}/accepter")
    public ResponseEntity<CandidatureResponse> accepter(@PathVariable Long id) {
        return ResponseEntity.ok(candidatureService.accepter(id));
    }

    @PatchMapping("/{id}/refuser")
    public ResponseEntity<CandidatureResponse> refuser(@PathVariable Long id) {
        return ResponseEntity.ok(candidatureService.refuser(id));
    }
}