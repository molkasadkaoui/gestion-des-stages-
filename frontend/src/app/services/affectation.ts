import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Affectation, AffectationRequest } from '../models/affectation.model';

@Injectable({ providedIn: 'root' })
export class AffectationService {
  private apiUrl = 'http://localhost:8081/api/affectations';

  constructor(private http: HttpClient) {}

  affecter(request: AffectationRequest): Observable<Affectation> {
    return this.http.post<Affectation>(this.apiUrl, request);
  }

  getAll(): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(this.apiUrl);
  }

  getByEncadrant(encadrantId: number): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(`${this.apiUrl}/encadrant/${encadrantId}`);
  }

  getByStagiaire(stagiaireId: number): Observable<Affectation[]> {
    return this.http.get<Affectation[]>(`${this.apiUrl}/stagiaire/${stagiaireId}`);
  }
}
