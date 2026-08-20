import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Candidature, CandidatureRequest } from '../models/candidature.model';

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

@Injectable({ providedIn: 'root' })
export class CandidatureService {
  private apiUrl = 'http://localhost:8081/api/candidatures';

  constructor(private http: HttpClient) {}

  postuler(request: CandidatureRequest): Observable<Candidature> {
    return this.http.post<Candidature>(this.apiUrl, request);
  }

  getAll(page = 0, size = 10): Observable<PageResponse<Candidature>> {
    return this.http.get<PageResponse<Candidature>>(this.apiUrl, { params: { page, size } });
  }

  getCandidaturesByStagiaire(stagiaireId: number, page = 0, size = 10): Observable<PageResponse<Candidature>> {
    return this.http.get<PageResponse<Candidature>>(`${this.apiUrl}/stagiaire/${stagiaireId}`, { params: { page, size } });
  }

  accepter(id: number): Observable<Candidature> {
    return this.http.patch<Candidature>(`${this.apiUrl}/${id}/accepter`, {});
  }

  refuser(id: number): Observable<Candidature> {
    return this.http.patch<Candidature>(`${this.apiUrl}/${id}/refuser`, {});
  }

  // NOUVELLE FONCTIONNALITÉ : Annuler une candidature
  annuler(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
