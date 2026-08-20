import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rapport, RapportRequest } from '../models/rapport.model';

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
export class RapportService {
  private apiUrl = 'http://localhost:8081/api/rapports';

  constructor(private http: HttpClient) {}

  deposer(request: RapportRequest): Observable<Rapport> {
    return this.http.post<Rapport>(this.apiUrl, request);
  }

  getAll(page = 0, size = 10): Observable<PageResponse<Rapport>> {
    return this.http.get<PageResponse<Rapport>>(this.apiUrl, { params: { page, size } });
  }

  getByAffectation(affectationId: number, page = 0, size = 10): Observable<PageResponse<Rapport>> {
    return this.http.get<PageResponse<Rapport>>(`${this.apiUrl}/affectation/${affectationId}`, { params: { page, size } });
  }

  mettreEnRevision(id: number): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/en-revision`, {});
  }

  valider(id: number): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/valider`, {});
  }

  rejeter(id: number): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/rejeter`, {});
  }

  getRapportsPourEncadrant(page = 0, size = 10): Observable<PageResponse<Rapport>> {
    return this.http.get<PageResponse<Rapport>>(`${this.apiUrl}/encadrant/mes-rapports`, { params: { page, size } });
  }

  validerRapport(id: number): Observable<Rapport> {
    return this.valider(id);
  }

  rejeterRapport(id: number): Observable<Rapport> {
    return this.rejeter(id);
  }

  // NOUVELLE FONCTIONNALITÉ : Resoumission d'un rapport rejeté
  resoumettre(id: number, request: RapportRequest): Observable<Rapport> {
    return this.http.put<Rapport>(`${this.apiUrl}/${id}`, request);
  }
}
