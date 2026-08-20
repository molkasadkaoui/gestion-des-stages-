-- Migration V11 : Réparer la table notifications (Flyway V7 n'a pas s'exécuté correctement)

-- Vérifier que la table notifications existe, sinon la créer
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

-- Recréer les indexes
DROP INDEX IF EXISTS idx_notifications_utilisateur;
DROP INDEX IF EXISTS idx_notifications_lu;
DROP INDEX IF EXISTS idx_notifications_date;

CREATE INDEX idx_notifications_utilisateur ON notifications(utilisateur_id);
CREATE INDEX idx_notifications_lu ON notifications(lu);
CREATE INDEX idx_notifications_date ON notifications(date_creation);
