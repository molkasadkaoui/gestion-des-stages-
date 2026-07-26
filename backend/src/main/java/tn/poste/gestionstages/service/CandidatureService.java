package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.CandidatureRequest;
import tn.poste.gestionstages.dto.CandidatureResponse;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.entity.Stagiaire;
import tn.poste.gestionstages.enums.StatutCandidature;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.repository.CandidatureRepository;
import tn.poste.gestionstages.repository.StageRepository;
import tn.poste.gestionstages.repository.StagiaireRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final StagiaireRepository stagiaireRepository;
    private final StageRepository stageRepository;

    public CandidatureResponse postuler(CandidatureRequest request) {
        Stagiaire stagiaire = stagiaireRepository.findById(request.getStagiaireId())
                .orElseThrow(() -> new RuntimeException("Stagiaire introuvable avec l'id : " + request.getStagiaireId()));

        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new RuntimeException("Stage introuvable avec l'id : " + request.getStageId()));

        if (stage.getStatut() != StatutStage.OUVERT) {
            throw new RuntimeException("Ce stage n'accepte plus de candidatures (statut : " + stage.getStatut() + ")");
        }

        if (candidatureRepository.existsByStagiaireIdAndStageId(stagiaire.getId(), stage.getId())) {
            throw new RuntimeException("Vous avez déjà postulé à ce stage");
        }

        Candidature candidature = new Candidature();
        candidature.setStagiaire(stagiaire);
        candidature.setStage(stage);
        candidature.setCvUrl(request.getCvUrl());
        candidature.setLettreMotivation(request.getLettreMotivation());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);

        Candidature saved = candidatureRepository.save(candidature);
        return toResponse(saved);
    }

    public List<CandidatureResponse> listerToutes() {
        return candidatureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CandidatureResponse> listerParStagiaire(Long stagiaireId) {
        return candidatureRepository.findByStagiaireId(stagiaireId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CandidatureResponse> listerParStage(Long stageId) {
        return candidatureRepository.findByStageId(stageId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CandidatureResponse accepter(Long id) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable avec l'id : " + id));
        candidature.setStatut(StatutCandidature.ACCEPTEE);
        return toResponse(candidatureRepository.save(candidature));
    }

    public CandidatureResponse refuser(Long id) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable avec l'id : " + id));
        candidature.setStatut(StatutCandidature.REFUSEE);
        return toResponse(candidatureRepository.save(candidature));
    }

    private CandidatureResponse toResponse(Candidature c) {
        return new CandidatureResponse(
                c.getId(),
                c.getStagiaire().getId(),
                c.getStagiaire().getUtilisateur().getNom() + " " + c.getStagiaire().getUtilisateur().getPrenom(),
                c.getStage().getId(),
                c.getStage().getTitre(),
                c.getDateCandidature(),
                c.getStatut(),
                c.getCvUrl(),
                c.getLettreMotivation()
        );
    }
}