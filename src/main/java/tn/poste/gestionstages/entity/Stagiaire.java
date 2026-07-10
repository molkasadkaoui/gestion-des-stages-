package tn.poste.gestionstages.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stagiaires")
@Getter
@Setter
@NoArgsConstructor
public class Stagiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String etablissement;

    @Column(name = "niveau_etude")
    private String niveauEtude;

    private String filiere;

    private String telephone;
}