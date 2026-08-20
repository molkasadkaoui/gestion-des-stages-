import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CandidatureService } from '../../services/candidature';
import { FileService } from '../../services/file';
import { NotificationService } from '../../services/notification';
import { Stage } from '../../models/stage.model';

@Component({
  selector: 'app-candidature-form',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatProgressSpinnerModule
  ],
  template: `
    <div class="candidature-form">
      <h2 mat-dialog-title>Postuler pour : {{ data.stage.titre }}</h2>

      <mat-dialog-content>
        <form #form="ngForm">

          <!-- Stage Info -->
          <div class="stage-info">
            <p><strong>Service :</strong> {{ data.stage.service }}</p>
            <p><strong>Type :</strong> {{ data.stage.typeStage }}</p>
            <p><strong>Dates :</strong> {{ data.stage.dateDebut | date:'dd/MM/yyyy' }} - {{ data.stage.dateFin | date:'dd/MM/yyyy' }}</p>
          </div>

          <!-- CV Upload -->
          <div class="file-upload-section">
            <label class="file-label">
              <input 
                type="file" 
                #fileInput 
                accept=".pdf,.doc,.docx" 
                (change)="onCVSelected($event)"
                hidden>
              <span class="file-label-content">
                <mat-icon>upload_file</mat-icon>
                <span>{{ cvFile ? 'CV: ' + cvFile.name : 'Télécharger votre CV (PDF, DOC, DOCX)' }}</span>
              </span>
            </label>
            @if (cvFile) {
              <button type="button" mat-icon-button (click)="removeCVFile()">
                <mat-icon>close</mat-icon>
              </button>
            }
          </div>

          <!-- Lettre de Motivation (Optionnelle) -->
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Lettre de motivation (optionnel)</mat-label>
            <mat-icon matPrefix>mail</mat-icon>
            <textarea matInput name="lettreMotivation" [(ngModel)]="candidature.lettreMotivation" 
              rows="4" placeholder="Expliquez pourquoi vous souhaitez ce stage (optionnel)..."></textarea>
          </mat-form-field>

          @if (error) {
            <p class="error">{{ error }}</p>
          }

        </form>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button (click)="onCancel()">Annuler</button>
        <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="loading || !cvFile">
          @if (loading) {
            <mat-spinner diameter="20" class="btn-spinner"></mat-spinner>
            <span>Envoi...</span>
          } @else {
            <mat-icon>send</mat-icon>
            <span>Postuler</span>
          }
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .candidature-form {
      padding: 20px;
      min-width: 500px;
    }
    .stage-info {
      background: #f5f5f5;
      padding: 15px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    .stage-info p {
      margin: 8px 0;
    }
    .file-upload-section {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 16px;
      padding: 12px;
      border: 2px dashed #ccc;
      border-radius: 4px;
      background: #fafafa;
    }
    .file-label {
      flex: 1;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .file-label-content {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #666;
      font-size: 14px;
    }
    .file-label:hover .file-label-content {
      color: #1976d2;
    }
    .full-width {
      width: 100%;
      margin-bottom: 16px;
    }
    .error {
      color: #d32f2f;
      margin: 16px 0;
      padding: 10px;
      background: #ffebee;
      border-radius: 4px;
    }
    mat-dialog-actions {
      margin-top: 24px;
    }
    button {
      margin-left: 8px;
    }
    .btn-spinner {
      margin-right: 8px;
    }
  `]
})
export class CandidatureFormComponent {
  cvFile: File | null = null;
  candidature = {
    lettreMotivation: ''
  };
  loading = false;
  error = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { stage: Stage; stagiaire: any },
    private candidatureService: CandidatureService,
    private fileService: FileService,
    private notificationService: NotificationService,
    private dialogRef: MatDialogRef<CandidatureFormComponent>
  ) {}

  onCVSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Vérifier la taille (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.error = 'Le fichier ne doit pas dépasser 5MB';
        return;
      }

      // Vérifier le format
      const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (!allowedTypes.includes(file.type)) {
        this.error = 'Format non accepté. Utilisez PDF, DOC ou DOCX';
        return;
      }

      this.cvFile = file;
      this.error = '';
    }
  }

  removeCVFile(): void {
    this.cvFile = null;
  }

  onSubmit(): void {
    if (!this.cvFile) {
      this.error = 'Veuillez télécharger votre CV';
      return;
    }

    this.error = '';
    this.loading = true;

    // D'abord, télécharger le CV
    this.fileService.upload(this.cvFile).subscribe({
      next: (uploadResponse) => {
        // Ensuite, créer la candidature
        const payload = {
          stageId: this.data.stage.id,
          cvUrl: uploadResponse.fileUrl,
          lettreMotivation: this.candidature.lettreMotivation || ''
        };

        this.candidatureService.postuler(payload).subscribe({
          next: () => {
            this.loading = false;
            this.notificationService.success('Candidature envoyée avec succès !');
            this.dialogRef.close(true);
          },
          error: (err) => {
            this.loading = false;
            this.error = err.error?.message || 'Erreur lors de la candidature';
            console.error(err);
          }
        });
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Erreur lors du téléchargement du CV';
        console.error(err);
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
