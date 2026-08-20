import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Affectation, AffectationRequest } from '../models/affectation.model';

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
export class AffectationService {
  private apiUrl = 'http://localhost:8081/api/affectations';

  constructor(private http: HttpClient) {}

  affecter(request: AffectationRequest): Observable<Affectation> {
    return this.http.post<Affectation>(this.apiUrl, request);
  }

  getAll(page = 0, size = 10): Observable<PageResponse<Affectation>> {
    return this.http.get<PageResponse<Affectation>>(this.apiUrl, { params: { page, size } });
  }

  getByEncadrant(encadrantId: number, page = 0, size = 10): Observable<PageResponse<Affectation>> {
    return this.http.get<PageResponse<Affectation>>(`${this.apiUrl}/encadrant/${encadrantId}`, { params: { page, size } });
  }

  getByStagiaire(stagiaireId: number, page = 0, size = 10): Observable<PageResponse<Affectation>> {
    return this.http.get<PageResponse<Affectation>>(`${this.apiUrl}/stagiaire/${stagiaireId}`, { params: { page, size } });
  }

  // NOUVELLE FONCTIONNALITÉ : Réaffecter un encadrant
  reaffecter(affectationId: number, nouvelEncadrantId: number): Observable<Affectation> {
    return this.http.patch<Affectation>(`${this.apiUrl}/${affectationId}/reaffecter/${nouvelEncadrantId}`, {});
  }

  // NOUVELLE FONCTIONNALITÉ : Supprimer une affectation
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les affectations de l'encadrant connecté
  listerMesAffectations(page = 0, size = 10): Observable<PageResponse<Affectation>> {
    return this.http.get<PageResponse<Affectation>>(`${this.apiUrl}/mes-affectations`, { params: { page, size } });
  }
}
