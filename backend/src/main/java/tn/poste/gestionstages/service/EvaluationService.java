package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.EvaluationRequest;
import tn.poste.gestionstages.dto.EvaluationResponse;
import tn.poste.gestionstages.entity.Affectation;
import tn.poste.gestionstages.entity.Evaluation;
import tn.poste.gestionstages.repository.AffectationRepository;
import tn.poste.gestionstages.repository.EvaluationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final AffectationRepository affectationRepository;

    public EvaluationResponse evaluer(EvaluationRequest request) {
        Affectation affectation = affectationRepository.findById(request.getAffectationId())
                .orElseThrow(() -> new RuntimeException("Affectation introuvable avec l'id : " + request.getAffectationId()));

        if (evaluationRepository.findByAffectationId(affectation.getId()).isPresent()) {
            throw new RuntimeException("Ce stagiaire a déjà été évalué pour cette affectation");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setAffectation(affectation);
        evaluation.setEncadrant(affectation.getEncadrant());
        evaluation.setNote(request.getNote());
        evaluation.setCommentaire(request.getCommentaire());

        Evaluation saved = evaluationRepository.save(evaluation);
        return toResponse(saved);
    }

    public List<EvaluationResponse> listerToutes() {
        return evaluationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EvaluationResponse getByAffectation(Long affectationId) {
        Evaluation evaluation = evaluationRepository.findByAffectationId(affectationId)
                .orElseThrow(() -> new RuntimeException("Aucune évaluation trouvée pour cette affectation"));
        return toResponse(evaluation);
    }

    private EvaluationResponse toResponse(Evaluation e) {
        return new EvaluationResponse(
                e.getId(),
                e.getAffectation().getId(),
                e.getAffectation().getCandidature().getStagiaire().getUtilisateur().getNom() + " " +
                        e.getAffectation().getCandidature().getStagiaire().getUtilisateur().getPrenom(),
                e.getEncadrant().getUtilisateur().getNom() + " " + e.getEncadrant().getUtilisateur().getPrenom(),
                e.getNote(),
                e.getCommentaire(),
                e.getDateEvaluation()
        );
    }
}