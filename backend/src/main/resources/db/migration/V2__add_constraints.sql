-- Migration pour ajouter les contraintes manquantes

-- Contrainte unique sur candidatures (stagiaire + stage)
-- Vérifier si la contrainte n'existe pas déjà
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'uk_candidature_stagiaire_stage'
    ) THEN
        ALTER TABLE candidatures 
        ADD CONSTRAINT uk_candidature_stagiaire_stage 
        UNIQUE (stagiaire_id, stage_id);
    END IF;
END $$;

-- Contrainte CHECK sur evaluations (note entre 0 et 20)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'chk_evaluation_note_range'
    ) THEN
        ALTER TABLE evaluations 
        ADD CONSTRAINT chk_evaluation_note_range 
        CHECK (note >= 0 AND note <= 20);
    END IF;
END $$;

-- Index sur les clés étrangères pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_candidatures_stagiaire ON candidatures(stagiaire_id);
CREATE INDEX IF NOT EXISTS idx_candidatures_stage ON candidatures(stage_id);
CREATE INDEX IF NOT EXISTS idx_candidatures_statut ON candidatures(statut);

CREATE INDEX IF NOT EXISTS idx_affectations_candidature ON affectations(candidature_id);
CREATE INDEX IF NOT EXISTS idx_affectations_encadrant ON affectations(encadrant_id);

CREATE INDEX IF NOT EXISTS idx_rapports_affectation ON rapports(affectation_id);
CREATE INDEX IF NOT EXISTS idx_rapports_statut ON rapports(statut);

CREATE INDEX IF NOT EXISTS idx_evaluations_affectation ON evaluations(affectation_id);
CREATE INDEX IF NOT EXISTS idx_evaluations_encadrant ON evaluations(encadrant_id);

CREATE INDEX IF NOT EXISTS idx_stages_statut ON stages(statut);
CREATE INDEX IF NOT EXISTS idx_stages_type ON stages(type_stage);
CREATE INDEX IF NOT EXISTS idx_stages_dates ON stages(date_debut, date_fin);
