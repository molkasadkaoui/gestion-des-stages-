package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.RapportRequest;
import tn.poste.gestionstages.dto.RapportResponse;
import tn.poste.gestionstages.service.RapportService;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @PostMapping
    public ResponseEntity<RapportResponse> deposer(@Valid @RequestBody RapportRequest request) {
        RapportResponse response = rapportService.deposer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RapportResponse>> listerTous() {
        return ResponseEntity.ok(rapportService.listerTous());
    }

    @GetMapping("/affectation/{affectationId}")
    public ResponseEntity<List<RapportResponse>> listerParAffectation(@PathVariable Long affectationId) {
        return ResponseEntity.ok(rapportService.listerParAffectation(affectationId));
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<RapportResponse> valider(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.valider(id));
    }

    @PatchMapping("/{id}/rejeter")
    public ResponseEntity<RapportResponse> rejeter(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.rejeter(id));
    }
}