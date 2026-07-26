package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.AffectationRequest;
import tn.poste.gestionstages.dto.AffectationResponse;
import tn.poste.gestionstages.entity.Affectation;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.entity.Encadrant;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.CandidatureRepository;
import tn.poste.gestionstages.repository.EncadrantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final CandidatureRepository candidatureRepository;
    private final EncadrantRepository encadrantRepository;

    public AffectationResponse affecter(AffectationRequest request) {
        Candidature candidature = candidatureRepository.findById(request.getCandidatureId())
                .orElseThrow(() -> new RuntimeException("Candidature introuvable avec l'id : " + request.getCandidatureId()));

        if (candidature.getStatut() != StatutCandidature.ACCEPTEE) {
            throw new RuntimeException("Seule une candidature acceptée peut être affectée à un encadrant");
        }

        if (affectationRepository.findByCandidatureId(candidature.getId()).isPresent()) {
            throw new RuntimeException("Cette candidature a déjà un encadrant affecté");
        }

        Encadrant encadrant = encadrantRepository.findById(request.getEncadrantId())
                .orElseThrow(() -> new RuntimeException("Encadrant introuvable avec l'id : " + request.getEncadrantId()));

        Affectation affectation = new Affectation();
        affectation.setCandidature(candidature);
        affectation.setEncadrant(encadrant);

        Affectation saved = affectationRepository.save(affectation);
        return toResponse(saved);
    }

    public List<AffectationResponse> listerToutes() {
        return affectationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AffectationResponse> listerParEncadrant(Long encadrantId) {
        return affectationRepository.findByEncadrantId(encadrantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AffectationResponse toResponse(Affectation a) {
        return new AffectationResponse(
                a.getId(),
                a.getCandidature().getId(),
                a.getCandidature().getStagiaire().getUtilisateur().getNom() + " " + a.getCandidature().getStagiaire().getUtilisateur().getPrenom(),
                a.getCandidature().getStage().getTitre(),
                a.getEncadrant().getId(),
                a.getEncadrant().getUtilisateur().getNom() + " " + a.getEncadrant().getUtilisateur().getPrenom(),
                a.getDateAffectation()
        );
    }
}