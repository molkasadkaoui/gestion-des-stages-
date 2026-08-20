package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.EncadrantResponse;
import tn.poste.gestionstages.entity.Encadrant;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.EncadrantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EncadrantService {

    private final EncadrantRepository encadrantRepository;
    private final AffectationRepository affectationRepository;

    public List<EncadrantResponse> listerTous() {
        return encadrantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EncadrantResponse getById(Long id) {
        Encadrant encadrant = encadrantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encadrant introuvable avec l'id : " + id));
        return toResponse(encadrant);
    }

    private EncadrantResponse toResponse(Encadrant e) {
        int charge = affectationRepository.findByEncadrantId(e.getId()).size();
        return new EncadrantResponse(
                e.getId(),
                e.getUtilisateur().getNom(),
                e.getUtilisateur().getPrenom(),
                e.getUtilisateur().getEmail(),
                e.getService(),
                e.getPoste(),
                e.getTelephone(),
                charge
        );
    }
}
