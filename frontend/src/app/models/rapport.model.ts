export interface RapportRequest {
  affectationId: number;
  fichierUrl: string;
  commentaire?: string;
}

export interface Rapport {
  id: number;
  affectationId: number;
  stagiaireNom: string;
  stageTitre: string;
  fichierUrl: string;
  dateDepot: string;
  dateSOumission?: string;
  dateValidation?: string;
  statut: 'DEPOSE' | 'EN_REVISION' | 'VALIDE' | 'REJETE';
  commentaire: string;
}
