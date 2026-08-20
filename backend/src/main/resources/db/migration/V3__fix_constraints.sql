-- Migration pour corriger les contraintes en conflit

-- Supprimer l'ancienne contrainte CHECK sur stages.nb_places si elle existe
ALTER TABLE stages DROP CONSTRAINT IF EXISTS chk_nb_places;

-- Ne pas recréer de contrainte CHECK sur nb_places car elle est gérée au niveau applicatif
-- La validation est faite dans StageRequest avec @Min(1) et @Max(50)
