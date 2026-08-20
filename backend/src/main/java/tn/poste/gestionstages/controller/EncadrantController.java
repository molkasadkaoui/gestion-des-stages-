package tn.poste.gestionstages.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.EncadrantResponse;
import tn.poste.gestionstages.service.EncadrantService;

import java.util.List;

@RestController
@RequestMapping("/api/encadrants")
@RequiredArgsConstructor
public class EncadrantController {

    private final EncadrantService encadrantService;

    @GetMapping
    public ResponseEntity<List<EncadrantResponse>> listerTous() {
        return ResponseEntity.ok(encadrantService.listerTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncadrantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(encadrantService.getById(id));
    }
}
