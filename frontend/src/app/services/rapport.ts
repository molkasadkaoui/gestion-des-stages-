import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rapport, RapportRequest } from '../models/rapport.model';

@Injectable({ providedIn: 'root' })
export class RapportService {
  private apiUrl = 'http://localhost:8081/api/rapports';

  constructor(private http: HttpClient) {}

  deposer(request: RapportRequest): Observable<Rapport> {
    return this.http.post<Rapport>(this.apiUrl, request);
  }

  getAll(): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(this.apiUrl);
  }

  getByAffectation(affectationId: number): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(`${this.apiUrl}/affectation/${affectationId}`);
  }

  valider(id: number): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/valider`, {});
  }

  rejeter(id: number): Observable<Rapport> {
    return this.http.patch<Rapport>(`${this.apiUrl}/${id}/rejeter`, {});
  }
}
