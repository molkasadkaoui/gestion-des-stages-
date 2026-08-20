export interface Notification {
  id: number;
  titre: string;
  message: string;
  type: 'CANDIDATURE' | 'AFFECTATION' | 'RAPPORT';
  lu: boolean;
  dateCreation: string;
  utilisateurId: number;
}

export interface NotificationResponse {
  content: Notification[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}
