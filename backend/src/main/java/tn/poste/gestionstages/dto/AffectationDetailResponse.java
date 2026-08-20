package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tn.poste.gestionstages.entity.Affectation;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AffectationDetailResponse {
    private Long id;
    private Long candidatureId;
    
    // Stagiaire details
    private Long stagiaireId;
    private String stagiaireNom;
    private String stagiairePrenom;
    private String stagiaireEmail;
    
    // Stage details
    private Long stageId;
    private String stageTitre;
    private String stageDescription;
    private String stageService;
    private String stageType;
    private LocalDate stageDebut;
    private LocalDate stageFin;
    private Integer stagePlaces;
    
    // Encadrant details
    private Long encadrantId;
    private String encadrantNom;
    private String encadrantPrenom;
    private String encadrantEmail;
    private String encadrantService;
    
    private LocalDateTime dateAffectation;

    public static AffectationDetailResponse from(Affectation affectation) {
        return AffectationDetailResponse.builder()
                .id(affectation.getId())
                .candidatureId(affectation.getCandidature().getId())
                
                .stagiaireId(affectation.getCandidature().getStagiaire().getId())
                .stagiaireNom(affectation.getCandidature().getStagiaire().getUtilisateur().getNom())
                .stagiairePrenom(affectation.getCandidature().getStagiaire().getUtilisateur().getPrenom())
                .stagiaireEmail(affectation.getCandidature().getStagiaire().getUtilisateur().getEmail())
                
                .stageId(affectation.getCandidature().getStage().getId())
                .stageTitre(affectation.getCandidature().getStage().getTitre())
                .stageDescription(affectation.getCandidature().getStage().getDescription())
                .stageService(affectation.getCandidature().getStage().getService())
                .stageType(affectation.getCandidature().getStage().getTypeStage().toString())
                .stageDebut(affectation.getCandidature().getStage().getDateDebut())
                .stageFin(affectation.getCandidature().getStage().getDateFin())
                .stagePlaces(affectation.getCandidature().getStage().getNbPlaces())
                
                .encadrantId(affectation.getEncadrant().getId())
                .encadrantNom(affectation.getEncadrant().getUtilisateur().getNom())
                .encadrantPrenom(affectation.getEncadrant().getUtilisateur().getPrenom())
                .encadrantEmail(affectation.getEncadrant().getUtilisateur().getEmail())
                .encadrantService(affectation.getEncadrant().getService())
                
                .dateAffectation(affectation.getDateAffectation())
                .build();
    }
}
