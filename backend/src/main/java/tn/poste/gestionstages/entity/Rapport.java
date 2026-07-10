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

    @ManyToOne
    @JoinColumn(name = "affectation_id", nullable = false)
    private Affectation affectation;

    @Column(name = "fichier_url", nullable = false)
    private String fichierUrl;

    @Column(name = "date_depot", updatable = false)
    private LocalDateTime dateDepot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRapport statut = StatutRapport.DEPOSE;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @PrePersist
    protected void onCreate() {
        this.dateDepot = LocalDateTime.now();
    }
}