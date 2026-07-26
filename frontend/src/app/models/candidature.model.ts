export interface CandidatureRequest {
  stagiaireId: number;
  stageId: number;
  cvUrl?: string;
  lettreMotivation?: string;
}

export interface Candidature {
  id: number;
  stagiaireId: number;
  stagiaireNom: string;
  stageId: number;
  stageTitre: string;
  dateCandidature: string;
  statut: 'EN_ATTENTE' | 'ACCEPTEE' | 'REFUSEE';
  cvUrl: string;
  lettreMotivation: string;
}
