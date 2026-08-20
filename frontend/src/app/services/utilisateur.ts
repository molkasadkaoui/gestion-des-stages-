import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  actif: boolean;
  approuve: boolean;
  dateCreation: string;
  dateApprobation?: string;
  telephone?: string;
  service?: string;
  etablissement?: string;
  niveauEtude?: string;
  filiere?: string;
  poste?: string;
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

export interface ChangePasswordRequest {
  ancienMotDePasse: string;
  nouveauMotDePasse: string;
  confirmationMotDePasse: string;
}

export interface UtilisateurUpdateRequest {
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  service?: string;
  etablissement?: string;
  niveauEtude?: string;
  filiere?: string;
  poste?: string;
  role?: string;
  actif?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private apiUrl = 'http://localhost:8081/api/utilisateurs';

  constructor(private http: HttpClient) {}

  // Obtenir tous les utilisateurs (ADMIN)
  obtenirTous(page = 0, size = 10): Observable<PageResponse<Utilisateur>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Utilisateur>>(this.apiUrl, { params });
  }

  // Obtenir mon profil
  obtenirMonProfil(): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/me`);
  }

  // Mettre à jour mon profil
  mettreAJourMonProfil(data: UtilisateurUpdateRequest): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.apiUrl}/me`, data);
  }

  // Changer mon mot de passe
  changerMotDePasse(data: ChangePasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/me/changer-mot-de-passe`, data);
  }

  // Obtenir un utilisateur par ID (ADMIN)
  obtenirParId(id: number): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/${id}`);
  }

  // Mettre à jour un utilisateur (ADMIN)
  mettreAJour(id: number, data: UtilisateurUpdateRequest): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.apiUrl}/${id}`, data);
  }

  // Supprimer un utilisateur (ADMIN)
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Désactiver un utilisateur (ADMIN)
  desactiver(id: number): Observable<Utilisateur> {
    return this.http.patch<Utilisateur>(`${this.apiUrl}/${id}/desactiver`, {});
  }

  // Activer un utilisateur (ADMIN)
  activer(id: number): Observable<Utilisateur> {
    return this.http.patch<Utilisateur>(`${this.apiUrl}/${id}/activer`, {});
  }

  // Lister les utilisateurs par rôle (ADMIN)
  obtenirParRole(role: string, page = 0, size = 10): Observable<PageResponse<Utilisateur>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Utilisateur>>(`${this.apiUrl}/role/${role}`, { params });
  }

  // Approuver un encadrant (ADMIN)
  appouverEncadrant(id: number): Observable<Utilisateur> {
    return this.http.patch<Utilisateur>(`${this.apiUrl}/${id}/approuver`, {});
  }

  // Rejeter un encadrant (ADMIN)
  rejeterEncadrant(id: number): Observable<Utilisateur> {
    return this.http.patch<Utilisateur>(`${this.apiUrl}/${id}/rejeter`, {});
  }
}
