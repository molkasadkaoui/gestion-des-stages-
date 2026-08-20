import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification, NotificationResponse } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationSystemService {
  private apiUrl = 'http://localhost:8081/api/notifications';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10): Observable<NotificationResponse> {
    return this.http.get<NotificationResponse>(this.apiUrl, { params: { page, size } });
  }

  marquerCommeLue(id: number): Observable<Notification> {
    return this.http.patch<Notification>(`${this.apiUrl}/${id}/marquer-comme-lue`, {});
  }

  marquerToutCommeLu(): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/marquer-tout-comme-lu`, {});
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
