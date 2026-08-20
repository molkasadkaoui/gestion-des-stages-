import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stage } from '../models/stage.model';

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
export class StageService {
  private apiUrl = 'http://localhost:8081/api/stages';

  constructor(private http: HttpClient) {}

  getStages(filters?: { statut?: string; typeStage?: string; service?: string }, page = 0, size = 10): Observable<PageResponse<Stage>> {
    let params = new HttpParams();
    if (filters?.statut)    params = params.set('statut', filters.statut);
    if (filters?.typeStage) params = params.set('typeStage', filters.typeStage);
    if (filters?.service)   params = params.set('service', filters.service);
    params = params.set('page', page.toString()).set('size', size.toString());
    return this.http.get<PageResponse<Stage>>(this.apiUrl, { params });
  }

  getStageById(id: number): Observable<Stage> {
    return this.http.get<Stage>(`${this.apiUrl}/${id}`);
  }

  createStage(stage: Partial<Stage>): Observable<Stage> {
    return this.http.post<Stage>(this.apiUrl, stage);
  }

  updateStage(id: number, stage: Partial<Stage>): Observable<Stage> {
    return this.http.put<Stage>(`${this.apiUrl}/${id}`, stage);
  }

  deleteStage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  fermer(id: number): Observable<Stage> {
    return this.http.patch<Stage>(`${this.apiUrl}/${id}/fermer`, {});
  }

  annuler(id: number): Observable<Stage> {
    return this.http.patch<Stage>(`${this.apiUrl}/${id}/annuler`, {});
  }

  rouvrir(id: number): Observable<Stage> {
    return this.http.patch<Stage>(`${this.apiUrl}/${id}/rouvrir`, {});
  }

  getMesStages(page = 0, size = 10): Observable<PageResponse<Stage>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Stage>>(`${this.apiUrl}/mes-stages`, { params });
  }
}
