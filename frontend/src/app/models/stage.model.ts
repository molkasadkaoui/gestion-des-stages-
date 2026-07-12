export interface Stage {
  id: number;
  titre: string;
  description: string;
  service: string;
  dateDebut: string;
  dateFin: string;
  typeStage: 'OBSERVATION' | 'INITIATION' | 'PFE' | 'ETE';
  statut: 'OUVERT' | 'FERME' | 'ANNULE';
  nbPlaces: number;
  dateCreation: string;
}
