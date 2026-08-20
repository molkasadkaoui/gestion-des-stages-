export interface CandidatureRequest {
  // stagiaireId supprimé - il est récupéré du token JWT côté backend
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
