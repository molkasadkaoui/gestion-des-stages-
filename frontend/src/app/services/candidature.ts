import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Candidature, CandidatureRequest } from '../models/candidature.model';

@Injectable({ providedIn: 'root' })
export class CandidatureService {
  private apiUrl = 'http://localhost:8081/api/candidatures';

  constructor(private http: HttpClient) {}

  postuler(request: CandidatureRequest): Observable<Candidature> {
    return this.http.post<Candidature>(this.apiUrl, request);
  }

  getAll(): Observable<Candidature[]> {
    return this.http.get<Candidature[]>(this.apiUrl);
  }

  getCandidaturesByStagiaire(stagiaireId: number): Observable<Candidature[]> {
    return this.http.get<Candidature[]>(`${this.apiUrl}/stagiaire/${stagiaireId}`);
  }

  accepter(id: number): Observable<Candidature> {
    return this.http.patch<Candidature>(`${this.apiUrl}/${id}/accepter`, {});
  }

  refuser(id: number): Observable<Candidature> {
    return this.http.patch<Candidature>(`${this.apiUrl}/${id}/refuser`, {});
  }
}
