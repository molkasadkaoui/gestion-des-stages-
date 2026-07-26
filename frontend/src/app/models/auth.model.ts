export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: 'ADMIN' | 'ENCADRANT' | 'STAGIAIRE';
  etablissement?: string;
  niveauEtude?: string;
  filiere?: string;
  telephone?: string;
  service?: string;
  poste?: string;
}

export interface AuthResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  profilId: number | null;
}
