package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.RapportRequest;
import tn.poste.gestionstages.dto.RapportResponse;
import tn.poste.gestionstages.entity.Affectation;
import tn.poste.gestionstages.entity.Rapport;
import tn.poste.gestionstages.enums.StatutRapport;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.RapportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;
    private final AffectationRepository affectationRepository;

    public RapportResponse deposer(RapportRequest request) {
        Affectation affectation = affectationRepository.findById(request.getAffectationId())
                .orElseThrow(() -> new RuntimeException("Affectation introuvable avec l'id : " + request.getAffectationId()));

        Rapport rapport = new Rapport();
        rapport.setAffectation(affectation);
        rapport.setFichierUrl(request.getFichierUrl());
        rapport.setCommentaire(request.getCommentaire());
        rapport.setStatut(StatutRapport.DEPOSE);

        Rapport saved = rapportRepository.save(rapport);
        return toResponse(saved);
    }

    public List<RapportResponse> listerParAffectation(Long affectationId) {
        return rapportRepository.findByAffectationId(affectationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RapportResponse> listerTous() {
        return rapportRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RapportResponse valider(Long id) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'id : " + id));
        rapport.setStatut(StatutRapport.VALIDE);
        return toResponse(rapportRepository.save(rapport));
    }

    public RapportResponse rejeter(Long id) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport introuvable avec l'id : " + id));
        rapport.setStatut(StatutRapport.REJETE);
        return toResponse(rapportRepository.save(rapport));
    }

    private RapportResponse toResponse(Rapport r) {
        return new RapportResponse(
                r.getId(),
                r.getAffectation().getId(),
                r.getAffectation().getCandidature().getStagiaire().getUtilisateur().getNom() + " " +
                        r.getAffectation().getCandidature().getStagiaire().getUtilisateur().getPrenom(),
                r.getFichierUrl(),
                r.getDateDepot(),
                r.getStatut(),
                r.getCommentaire()
        );
    }
}