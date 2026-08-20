-- Migration V13 : S'assurer que toutes les tables existent (correction finale)

-- Supprimer la table rapports ancienne version pour la recréer correctement
DROP TABLE IF EXISTS rapports CASCADE;

-- Table notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    lu BOOLEAN NOT NULL DEFAULT false,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Index pour notifications
CREATE INDEX IF NOT EXISTS idx_notifications_utilisateur ON notifications(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_notifications_lu ON notifications(lu);
CREATE INDEX IF NOT EXISTS idx_notifications_date ON notifications(date_creation);

-- Table rapports (nouvelle version correcte)
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

-- Index pour rapports
CREATE INDEX IF NOT EXISTS idx_rapports_affectation ON rapports(affectation_id);
CREATE INDEX IF NOT EXISTS idx_rapports_statut ON rapports(statut);
