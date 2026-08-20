package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.RapportRequest;
import tn.poste.gestionstages.dto.RapportResponse;
import tn.poste.gestionstages.service.RapportService;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @PostMapping
    public ResponseEntity<RapportResponse> deposerRapport(@Valid @RequestBody RapportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rapportService.deposerRapport(request));
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<RapportResponse> validerRapport(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.validerRapport(id));
    }

    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<RapportResponse> rejeterRapport(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.rejeterRapport(id));
    }

    @GetMapping("/mes-rapports")
    public ResponseEntity<Page<RapportResponse>> mesRapports(Pageable pageable) {
        return ResponseEntity.ok(rapportService.meseRapports(pageable));
    }

    @GetMapping("/mes-stagiaires")
    public ResponseEntity<Page<RapportResponse>> rapportsPourEncadrant(Pageable pageable) {
        return ResponseEntity.ok(rapportService.rapportsPourEncadrant(pageable));
    }
}
