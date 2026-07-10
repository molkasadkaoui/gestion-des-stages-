package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.dto.StageRequest;
import tn.poste.gestionstages.dto.StageResponse;
import tn.poste.gestionstages.entity.Stage;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.repository.StageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    public StageResponse creerStage(StageRequest request) {
        Stage stage = new Stage();
        stage.setTitre(request.getTitre());
        stage.setDescription(request.getDescription());
        stage.setService(request.getService());
        stage.setDateDebut(request.getDateDebut());
        stage.setDateFin(request.getDateFin());
        stage.setTypeStage(request.getTypeStage());
        stage.setNbPlaces(request.getNbPlaces());
        stage.setStatut(StatutStage.OUVERT);

        Stage saved = stageRepository.save(stage);
        return toResponse(saved);
    }

    public List<StageResponse> listerStages() {
        return stageRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StageResponse getStageById(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage introuvable avec l'id : " + id));
        return toResponse(stage);
    }

    public StageResponse modifierStage(Long id, StageRequest request) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stage introuvable avec l'id : " + id));

        stage.setTitre(request.getTitre());
        stage.setDescription(request.getDescription());
        stage.setService(request.getService());
        stage.setDateDebut(request.getDateDebut());
        stage.setDateFin(request.getDateFin());
        stage.setTypeStage(request.getTypeStage());
        stage.setNbPlaces(request.getNbPlaces());

        Stage updated = stageRepository.save(stage);
        return toResponse(updated);
    }

    public void supprimerStage(Long id) {
        if (!stageRepository.existsById(id)) {
            throw new RuntimeException("Stage introuvable avec l'id : " + id);
        }
        stageRepository.deleteById(id);
    }

    private StageResponse toResponse(Stage stage) {
        return new StageResponse(
                stage.getId(),
                stage.getTitre(),
                stage.getDescription(),
                stage.getService(),
                stage.getDateDebut(),
                stage.getDateFin(),
                stage.getTypeStage(),
                stage.getStatut(),
                stage.getNbPlaces(),
                stage.getDateCreation()
        );
    }
}