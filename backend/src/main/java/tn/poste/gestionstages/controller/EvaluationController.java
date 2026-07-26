package tn.poste.gestionstages.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.EvaluationRequest;
import tn.poste.gestionstages.dto.EvaluationResponse;
import tn.poste.gestionstages.service.EvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<EvaluationResponse> evaluer(@Valid @RequestBody EvaluationRequest request) {
        EvaluationResponse response = evaluationService.evaluer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> listerToutes() {
        return ResponseEntity.ok(evaluationService.listerToutes());
    }

    @GetMapping("/affectation/{affectationId}")
    public ResponseEntity<EvaluationResponse> getByAffectation(@PathVariable Long affectationId) {
        return ResponseEntity.ok(evaluationService.getByAffectation(affectationId));
    }
}