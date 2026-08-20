import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { RapportService } from '../../services/rapport';
import { AffectationService } from '../../services/affectation';
import { AuthService } from '../../services/auth';
import { FileService } from '../../services/file';
import { RapportRequest } from '../../models/rapport.model';
import { Affectation } from '../../models/affectation.model';

@Component({
  selector: 'app-rapport-form',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule,
    MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatProgressSpinnerModule, TranslatePipe
  ],
  templateUrl: './rapport-form.html',
  styleUrl: './rapport-form.css'
})
export class RapportForm implements OnInit {
  affectation: Affectation | null = null;
  checkingAffectation = true;

  selectedFile: File | null = null;
  request: Omit<RapportRequest, 'affectationId'> = { fichierUrl: '', commentaire: '' };
  error = '';
  success = '';
  loading = false;

  constructor(
    private rapportService: RapportService,
    private affectationService: AffectationService,
    private authService: AuthService,
    private fileService: FileService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.checkingAffectation = false;
      this.cdr.detectChanges();
      return;
    }

    this.affectationService.getByStagiaire(user.profilId, 0, 100).subscribe({
      next: (data) => {
        this.affectation = data.content.length > 0 ? data.content[0] : null;
        this.checkingAffectation = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors de la récupération de l\'affectation:', err);
        this.checkingAffectation = false;
        this.cdr.detectChanges();
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.error = '';
      console.log('Fichier sélectionné:', this.selectedFile.name);
      this.cdr.detectChanges();
    }
  }

  onSubmit(): void {
    console.log('onSubmit appelé');
    console.log('Affectation:', this.affectation);
    console.log('Fichier sélectionné:', this.selectedFile);
    
    if (!this.affectation) {
      this.error = "Vous devez être affecté(e) à un stage pour pouvoir déposer un rapport.";
      return;
    }

    if (!this.selectedFile && !this.request.fichierUrl) {
      this.error = 'Veuillez sélectionner un fichier de rapport depuis votre ordinateur.';
      return;
    }

    this.error = '';
    this.success = '';
    this.loading = true;
    this.cdr.detectChanges();

    if (this.selectedFile) {
      console.log('Téléchargement du fichier...');
      // 1. Téléverser le fichier sur le serveur backend
      this.fileService.upload(this.selectedFile).subscribe({
        next: (res) => {
          console.log('Fichier téléchargé, URL:', res.fileUrl);
          // 2. Déposer le rapport avec le lien du fichier téléversé
          this.deposerRapport(res.fileUrl);
        },
        error: (err) => {
          this.loading = false;
          if (err.status === 0) {
            this.error = 'Le serveur Backend (Spring Boot) n\'est pas démarré sur le port 8081.';
          } else if (err.status === 403 || err.status === 401) {
            this.error = 'Session expirée. Veuillez vous reconnecter.';
          } else {
            this.error = 'Erreur lors du téléversement du fichier : ' + (err.error?.message || err.message || 'Erreur réseau');
          }
          console.error('Erreur upload:', err);
          this.cdr.detectChanges();
        }
      });
    } else {
      console.log('Dépôt du rapport avec URL fournie');
      this.deposerRapport(this.request.fichierUrl);
    }
  }

  private deposerRapport(fileUrl: string): void {
    if (!this.affectation) return;

    this.rapportService.deposer({
      affectationId: this.affectation.id,
      fichierUrl: fileUrl,
      commentaire: this.request.commentaire
    }).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Rapport déposé avec succès !';
        this.request = { fichierUrl: '', commentaire: '' };
        this.selectedFile = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 0) {
          this.error = 'Le serveur Backend (Spring Boot) n\'est pas démarré sur le port 8081.';
        } else if (err.status === 403 || err.status === 401) {
          this.error = 'Droits insuffisants ou session expirée. Seul un Stagiaire connecté peut déposer un rapport.';
        } else {
          this.error = err.error?.message || 'Erreur lors du dépôt du rapport.';
        }
        this.cdr.detectChanges();
      }
    });
  }
}
