package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.CandidatureRequest;
import tn.poste.gestionstages.dto.CandidatureResponse;
import tn.poste.gestionstages.service.CandidatureService;
import tn.poste.gestionstages.service.FileStorageService;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<CandidatureResponse> postuler(@Valid @RequestBody CandidatureRequest request) {
        CandidatureResponse response = candidatureService.postuler(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CandidatureResponse>> listerToutes(Pageable pageable) {
        return ResponseEntity.ok(candidatureService.listerToutes(pageable));
    }

    @GetMapping("/stagiaire/{stagiaireId}")
    public ResponseEntity<Page<CandidatureResponse>> listerParStagiaire(@PathVariable Long stagiaireId, Pageable pageable) {
        return ResponseEntity.ok(candidatureService.listerParStagiaire(stagiaireId, pageable));
    }

    @GetMapping("/stage/{stageId}")
    public ResponseEntity<Page<CandidatureResponse>> listerParStage(@PathVariable Long stageId, Pageable pageable) {
        return ResponseEntity.ok(candidatureService.listerParStage(stageId, pageable));
    }

    @PatchMapping("/{id}/accepter")
    public ResponseEntity<CandidatureResponse> accepter(@PathVariable Long id) {
        return ResponseEntity.ok(candidatureService.accepter(id));
    }

    @PatchMapping("/{id}/refuser")
    public ResponseEntity<CandidatureResponse> refuser(@PathVariable Long id) {
        return ResponseEntity.ok(candidatureService.refuser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annuler(@PathVariable Long id) {
        candidatureService.annuler(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint pour télécharger le CV d'une candidature
     */
    @GetMapping("/{candidatureId}/cv/download")
    public ResponseEntity<Resource> downloadCandidatureCV(@PathVariable Long candidatureId) {
        return candidatureService.downloadCandidatureCV(candidatureId);
    }
}