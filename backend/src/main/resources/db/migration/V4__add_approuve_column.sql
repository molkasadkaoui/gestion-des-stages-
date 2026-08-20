-- Ajouter les colonnes approuve et date_approbation à la table utilisateurs
ALTER TABLE utilisateurs ADD COLUMN approuve BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE utilisateurs ADD COLUMN date_approbation TIMESTAMP NULL;

-- Les stagiaires et admins sont approuvés par défaut
UPDATE utilisateurs SET approuve = true WHERE role IN ('STAGIAIRE', 'ADMIN');
