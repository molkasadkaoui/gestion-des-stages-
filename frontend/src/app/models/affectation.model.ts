export interface AffectationRequest {
  candidatureId: number;
  encadrantId: number;
}

export interface Affectation {
  id: number;
  candidatureId: number;
  stagiaireNom: string;
  stageTitre: string;
  encadrantId: number;
  encadrantNom: string;
  dateAffectation: string;
}
