-- Migration V8 : Table rapports complète

-- Ajouter les colonnes manquantes à la table rapports si elle existe
ALTER TABLE rapports ADD COLUMN IF NOT EXISTS fichier_url VARCHAR(255);
ALTER TABLE rapports ADD COLUMN IF NOT EXISTS commentaire TEXT;

-- Supprimer la table pour la recréer proprement
DROP TABLE IF EXISTS rapports CASCADE;

CREATE TABLE rapports (
    id BIGSERIAL PRIMARY KEY,
    affectation_id BIGINT NOT NULL UNIQUE,
    fichier_url VARCHAR(255) NOT NULL,
    commentaire TEXT,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    date_soumission TIMESTAMP,
    date_validation TIMESTAMP,
    CONSTRAINT fk_rapport_affectation FOREIGN KEY (affectation_id) REFERENCES affectations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_rapports_affectation ON rapports(affectation_id);
CREATE INDEX IF NOT EXISTS idx_rapports_statut ON rapports(statut);

