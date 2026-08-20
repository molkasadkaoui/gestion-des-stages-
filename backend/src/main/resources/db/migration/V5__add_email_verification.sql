-- Migration V5 : Ajouter la vérification d'email

ALTER TABLE utilisateurs ADD COLUMN email_verifie BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE utilisateurs ADD COLUMN token_verification VARCHAR(500);
ALTER TABLE utilisateurs ADD COLUMN date_verification TIMESTAMP NULL;

-- Les utilisateurs existants sont automatiquement marqués comme vérifiés
UPDATE utilisateurs SET email_verifie = true WHERE id > 0;
