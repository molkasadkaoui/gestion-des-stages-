export interface RapportRequest {
  affectationId: number;
  fichierUrl: string;
  commentaire?: string;
}

export interface Rapport {
  id: number;
  affectationId: number;
  stagiaireNom: string;
  fichierUrl: string;
  dateDepot: string;
  statut: 'DEPOSE' | 'EN_REVISION' | 'VALIDE' | 'REJETE';
  commentaire: string;
}
