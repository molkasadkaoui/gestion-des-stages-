package tn.poste.gestionstages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import tn.poste.gestionstages.enums.StatutRapport;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Getter
@Setter
@NoArgsConstructor
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "affectation_id", nullable = false, unique = true)
    private Affectation affectation;

    @Column(name = "fichier_url", nullable = false)
    private String fichierUrl;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRapport statut = StatutRapport.EN_ATTENTE;

    @Column(name = "date_soumission", updatable = false)
    private LocalDateTime dateSOumission;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @PrePersist
    protected void onCreate() {
        this.dateSOumission = LocalDateTime.now();
    }
}