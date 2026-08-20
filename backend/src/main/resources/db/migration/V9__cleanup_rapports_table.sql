-- Migration V9 : Nettoyer et recréer la table rapports correctement

-- Vider les données de la table rapports
DELETE FROM rapports;

-- Réinitialiser la séquence si elle existe
ALTER SEQUENCE IF EXISTS rapports_id_seq RESTART WITH 1;
