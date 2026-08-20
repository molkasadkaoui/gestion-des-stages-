import { Stage } from './stage.model';

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
}

export interface AffectationRequest {
  candidatureId: number;
  encadrantId: number;
}

export interface Affectation {
  id: number;
  candidatureId: number;
  // Stagiaire details
  stagiaireId?: number;
  stagiaireNom?: string;
  stagiairePrenom?: string;
  stagiaireEmail?: string;
  // Stage details
  stageId?: number;
  stageTitre: string;
  stageDescription?: string;
  stageService?: string;
  stageType?: string;
  stageDebut?: string;
  stageFin?: string;
  stagePlaces?: number;
  // Encadrant details
  encadrantId: number;
  encadrantNom: string;
  encadrantPrenom?: string;
  encadrantEmail?: string;
  encadrantService?: string;
  dateAffectation: string;
  // Champs hérités (optionnels, pour compatibilité)
  stagiaire?: User;
  stage?: Stage;
  encadrant?: User;
}
