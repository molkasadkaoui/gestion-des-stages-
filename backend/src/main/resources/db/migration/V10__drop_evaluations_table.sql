-- Migration V10 : Supprimer la table evaluations (système d'évaluation désactivé)

-- Supprimer les contraintes de clé étrangère si elles existent
ALTER TABLE IF EXISTS evaluations
DROP CONSTRAINT IF EXISTS fk_evaluations_affectation_id CASCADE;

-- Supprimer la table evaluations
DROP TABLE IF EXISTS evaluations CASCADE;

-- Supprimer la séquence si elle existe
DROP SEQUENCE IF EXISTS evaluations_id_seq;
