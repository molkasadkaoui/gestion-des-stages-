-- Migration V12 : Réparer la table rapports (Flyway V8 n'a pas s'exécuté correctement)

-- Créer la table rapports si elle n'existe pas
CREATE TABLE IF NOT EXISTS rapports (
    id BIGSERIAL PRIMARY KEY,
    affectation_id BIGINT NOT NULL UNIQUE,
    fichier_url VARCHAR(255) NOT NULL,
    commentaire TEXT,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE',
    date_soumission TIMESTAMP,
    date_validation TIMESTAMP,
    CONSTRAINT fk_rapport_affectation FOREIGN KEY (affectation_id) REFERENCES affectations(id) ON DELETE CASCADE
);

-- Recréer les indexes
DROP INDEX IF EXISTS idx_rapports_affectation;
DROP INDEX IF EXISTS idx_rapports_statut;

CREATE INDEX idx_rapports_affectation ON rapports(affectation_id);
CREATE INDEX idx_rapports_statut ON rapports(statut);
