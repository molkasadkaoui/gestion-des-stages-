-- Migration V1 : Création du schéma initial

-- Table utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT true,
    approuve BOOLEAN NOT NULL DEFAULT false,
    date_creation TIMESTAMP,
    date_approbation TIMESTAMP
);

-- Table stagiaires
CREATE TABLE IF NOT EXISTS stagiaires (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    etablissement VARCHAR(255),
    niveau_etude VARCHAR(255),
    filiere VARCHAR(255),
    telephone VARCHAR(20),
    CONSTRAINT fk_stagiaire_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

-- Table encadrants
CREATE TABLE IF NOT EXISTS encadrants (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    service VARCHAR(255),
    poste VARCHAR(255),
    telephone VARCHAR(20),
    CONSTRAINT fk_encadrant_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

-- Table stages
CREATE TABLE IF NOT EXISTS stages (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nb_places INTEGER NOT NULL,
    type_stage VARCHAR(50),
    statut VARCHAR(50),
    date_creation TIMESTAMP
);

-- Table candidatures
CREATE TABLE IF NOT EXISTS candidatures (
    id BIGSERIAL PRIMARY KEY,
    stagiaire_id BIGINT NOT NULL,
    stage_id BIGINT NOT NULL,
    statut VARCHAR(50),
    date_candidature TIMESTAMP,
    CONSTRAINT fk_candidature_stagiaire FOREIGN KEY (stagiaire_id) REFERENCES stagiaires(id),
    CONSTRAINT fk_candidature_stage FOREIGN KEY (stage_id) REFERENCES stages(id)
);

-- Table affectations
CREATE TABLE IF NOT EXISTS affectations (
    id BIGSERIAL PRIMARY KEY,
    candidature_id BIGINT NOT NULL,
    encadrant_id BIGINT NOT NULL,
    date_affectation TIMESTAMP,
    CONSTRAINT fk_affectation_candidature FOREIGN KEY (candidature_id) REFERENCES candidatures(id),
    CONSTRAINT fk_affectation_encadrant FOREIGN KEY (encadrant_id) REFERENCES encadrants(id)
);

-- Table rapports
CREATE TABLE IF NOT EXISTS rapports (
    id BIGSERIAL PRIMARY KEY,
    affectation_id BIGINT NOT NULL,
    contenu TEXT,
    statut VARCHAR(50),
    date_soumission TIMESTAMP,
    CONSTRAINT fk_rapport_affectation FOREIGN KEY (affectation_id) REFERENCES affectations(id)
);

-- Table evaluations
CREATE TABLE IF NOT EXISTS evaluations (
    id BIGSERIAL PRIMARY KEY,
    affectation_id BIGINT NOT NULL,
    encadrant_id BIGINT NOT NULL,
    note NUMERIC(5, 2),
    commentaire TEXT,
    date_evaluation TIMESTAMP,
    CONSTRAINT fk_evaluation_affectation FOREIGN KEY (affectation_id) REFERENCES affectations(id),
    CONSTRAINT fk_evaluation_encadrant FOREIGN KEY (encadrant_id) REFERENCES encadrants(id)
);

-- Créer les index de base
CREATE INDEX IF NOT EXISTS idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_role ON utilisateurs(role);
CREATE INDEX IF NOT EXISTS idx_stagiaires_utilisateur ON stagiaires(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_encadrants_utilisateur ON encadrants(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_stages_statut ON stages(statut);
CREATE INDEX IF NOT EXISTS idx_candidatures_stagiaire ON candidatures(stagiaire_id);
CREATE INDEX IF NOT EXISTS idx_candidatures_stage ON candidatures(stage_id);
CREATE INDEX IF NOT EXISTS idx_affectations_candidature ON affectations(candidature_id);
CREATE INDEX IF NOT EXISTS idx_affectations_encadrant ON affectations(encadrant_id);
CREATE INDEX IF NOT EXISTS idx_rapports_affectation ON rapports(affectation_id);
CREATE INDEX IF NOT EXISTS idx_evaluations_affectation ON evaluations(affectation_id);
