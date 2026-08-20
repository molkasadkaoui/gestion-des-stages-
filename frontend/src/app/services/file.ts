import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FileUploadResponse {
  fileName: string;
  fileUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = 'http://localhost:8081/api/files';

  constructor(private http: HttpClient) {}

  upload(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Télécharger le PDF d'une candidature
   */
  downloadCandidaturePDF(candidatureId: number): Observable<Blob> {
    return this.http.get(
      `${this.apiUrl}/candidature/${candidatureId}/pdf`,
      { responseType: 'blob' }
    );
  }
}
