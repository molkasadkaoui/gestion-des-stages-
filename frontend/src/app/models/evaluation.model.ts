export interface EvaluationRequest {
  affectationId: number;
  note: number;
  commentaire?: string;
}

export interface Evaluation {
  id: number;
  affectationId: number;
  stagiaireNom: string;
  encadrantNom: string;
  note: number;
  commentaire: string;
  dateEvaluation: string;
}
