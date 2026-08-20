import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Encadrant } from '../models/encadrant.model';

@Injectable({ providedIn: 'root' })
export class EncadrantService {
  private apiUrl = 'http://localhost:8081/api/encadrants';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Encadrant[]> {
    return this.http.get<Encadrant[]>(this.apiUrl);
  }

  getById(id: number): Observable<Encadrant> {
    return this.http.get<Encadrant>(`${this.apiUrl}/${id}`);
  }
}
