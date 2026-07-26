import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evaluation, EvaluationRequest } from '../models/evaluation.model';

@Injectable({ providedIn: 'root' })
export class EvaluationService {
  private apiUrl = 'http://localhost:8081/api/evaluations';

  constructor(private http: HttpClient) {}

  evaluer(request: EvaluationRequest): Observable<Evaluation> {
    return this.http.post<Evaluation>(this.apiUrl, request);
  }

  getAll(): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(this.apiUrl);
  }
}
