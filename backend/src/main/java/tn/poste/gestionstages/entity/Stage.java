package tn.poste.gestionstages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tn.poste.gestionstages.enums.StatutStage;
import tn.poste.gestionstages.enums.TypeStage;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stages")
@Getter
@Setter
@NoArgsConstructor
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String service;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_stage", nullable = false)
    private TypeStage typeStage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutStage statut = StatutStage.OUVERT;

    @Column(name = "nb_places", nullable = false)
    private Integer nbPlaces = 1;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}