package tn.poste.gestionstages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "affectation_id", nullable = false, unique = true)
    private Affectation affectation;

    @ManyToOne
    @JoinColumn(name = "encadrant_id", nullable = false)
    private Encadrant encadrant;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_evaluation", updatable = false)
    private LocalDateTime dateEvaluation;

    @PrePersist
    protected void onCreate() {
        this.dateEvaluation = LocalDateTime.now();
    }
}