import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { Candidature } from '../../models/candidature.model';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-candidature-detail-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule, MatButtonModule, MatIconModule, MatTabsModule,
    MatProgressSpinnerModule, TranslatePipe
  ],
  template: `
    <div class="detail-dialog">
      <h2 mat-dialog-title>Détails de la candidature</h2>

      <mat-dialog-content>
        <!-- Informations du stagiaire et du stage -->
        <div class="info-section">
          <div class="info-row">
            <span class="label">Stagiaire :</span>
            <span class="value">{{ data.stagiaireNom }}</span>
          </div>
          <div class="info-row">
            <span class="label">Stage :</span>
            <span class="value">{{ data.stageTitre }}</span>
          </div>
          <div class="info-row">
            <span class="label">Date de candidature :</span>
            <span class="value">{{ data.dateCandidature | date:'dd/MM/yyyy à HH:mm' }}</span>
          </div>
          <div class="info-row">
            <span class="label">Statut :</span>
            <span class="status-chip" [ngClass]="'status-' + data.statut">
              {{ ('CANDIDATURES.STATUT_' + data.statut) | translate }}
            </span>
          </div>
        </div>

        <!-- Onglets pour CV et Lettre -->
        <mat-tab-group>
          <!-- Tab CV -->
          <mat-tab label="CV" (click)="selectedTab = 'cv'">
            <ng-template mat-tab-label>
              <mat-icon>description</mat-icon>
              <span>CV</span>
            </ng-template>
            <div class="tab-content">
              @if (data.cvUrl && data.cvUrl.trim()) {
                <div class="cv-viewer">
                  <p class="cv-notice">{{ 'CANDIDATURES.CV_UPLOADED' | translate }}</p>
                  <button mat-raised-button class="cv-link" (click)="downloadCV()">
                    <mat-icon>download</mat-icon>
                    {{ 'CANDIDATURES.DOWNLOAD_CV' | translate }}
                  </button>
                </div>
              } @else {
                <div class="empty-notice">
                  <mat-icon>description</mat-icon>
                  {{ 'CANDIDATURES.NO_CV' | translate }}
                </div>
              }
            </div>
          </mat-tab>

          <!-- Tab Lettre de Motivation -->
          <mat-tab label="Lettre" (click)="selectedTab = 'lettre'">
            <ng-template mat-tab-label>
              <mat-icon>mail</mat-icon>
              <span>Lettre de motivation</span>
            </ng-template>
            <div class="tab-content">
              @if (data.lettreMotivation && data.lettreMotivation.trim()) {
                <div class="lettre-viewer">
                  <p class="lettre-text">{{ data.lettreMotivation }}</p>
                </div>
              } @else {
                <div class="empty-notice">
                  <mat-icon>mail_outline</mat-icon>
                  {{ 'CANDIDATURES.NO_LETTRE' | translate }}
                </div>
              }
            </div>
          </mat-tab>
        </mat-tab-group>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button (click)="onCancel()">{{ 'COMMON.CLOSE' | translate }}</button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .detail-dialog {
      min-width: 600px;
    }

    h2 {
      margin: 0 0 20px 0;
    }

    mat-dialog-content {
      max-height: 600px;
      overflow-y: auto;
    }

    .info-section {
      background: #f5f5f5;
      padding: 16px;
      border-radius: 4px;
      margin-bottom: 20px;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px solid #e0e0e0;
    }

    .info-row:last-child {
      border-bottom: none;
    }

    .label {
      font-weight: 600;
      color: #333;
      min-width: 150px;
    }

    .value {
      color: #666;
      flex: 1;
      text-align: right;
    }

    .status-chip {
      display: inline-block;
      padding: 4px 12px;
      border-radius: 16px;
      font-size: 12px;
      font-weight: 600;
      text-align: right;
    }

    .status-EN_ATTENTE {
      background: #fff3cd;
      color: #856404;
    }

    .status-ACCEPTEE {
      background: #d4edda;
      color: #155724;
    }

    .status-REFUSEE {
      background: #f8d7da;
      color: #721c24;
    }

    mat-tab-group {
      margin-top: 20px;
    }

    .tab-content {
      padding: 20px;
    }

    .cv-viewer,
    .lettre-viewer {
      background: #f9f9f9;
      padding: 16px;
      border-radius: 4px;
      border: 1px solid #e0e0e0;
    }

    .cv-notice,
    .empty-notice {
      margin: 0 0 16px 0;
      color: #666;
    }

    .cv-link {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 12px 16px;
      background: #1976d2;
      color: white;
      text-decoration: none;
      border-radius: 4px;
      font-weight: 600;
      transition: background 0.3s;
    }

    .cv-link:hover {
      background: #1565c0;
    }

    .lettre-text {
      white-space: pre-wrap;
      line-height: 1.6;
      color: #333;
      margin: 0;
    }

    .empty-notice {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 40px 20px;
      color: #999;
      text-align: center;
    }

    .empty-notice mat-icon {
      font-size: 48px;
      width: 48px;
      height: 48px;
      opacity: 0.5;
    }

    mat-dialog-actions {
      margin-top: 20px;
    }
  `]
})
export class CandidatureDetailDialogComponent {
  selectedTab: string = 'cv';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: Candidature,
    private dialogRef: MatDialogRef<CandidatureDetailDialogComponent>,
    private http: HttpClient,
    private notificationService: NotificationService
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  downloadCV(): void {
    // Utiliser le nouvel endpoint du backend
    const apiUrl = `http://localhost:8081/api/candidatures/${this.data.id}/cv/download`;
    
    this.http.get(apiUrl, { responseType: 'blob' }).subscribe({
      next: (blob: Blob) => {
        // Créer un blob URL et télécharger
        const link = document.createElement('a');
        const url = window.URL.createObjectURL(blob);
        link.href = url;
        
        // Déterminer l'extension du fichier
        let fileName = 'CV';
        if (this.data.cvUrl) {
          if (this.data.cvUrl.endsWith('.pdf')) fileName += '.pdf';
          else if (this.data.cvUrl.endsWith('.docx')) fileName += '.docx';
          else if (this.data.cvUrl.endsWith('.doc')) fileName += '.doc';
        }
        
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        
        this.notificationService.success('CV téléchargé avec succès');
      },
      error: (err) => {
        console.error(err);
        this.notificationService.error('Erreur lors du téléchargement du CV');
      }
    });
  }
}
