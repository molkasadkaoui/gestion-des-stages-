import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Stage } from '../../models/stage.model';
import { StageService } from '../../services/stage';
import { CandidatureService } from '../../services/candidature';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-stage-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, MatTableModule, MatChipsModule,
    MatProgressSpinnerModule, MatButtonModule
  ],
  templateUrl: './stage-list.html',
  styleUrl: './stage-list.css'
})
export class StageList implements OnInit {
  stages: Stage[] = [];
  loading = true;
  error = '';
  displayedColumns: string[] = ['titre', 'service', 'typeStage', 'statut', 'nbPlaces', 'dateDebut', 'dateFin', 'actions'];

  constructor(
    private stageService: StageService,
    private candidatureService: CandidatureService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.stageService.getStages().subscribe({
      next: (data) => {
        this.stages = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des stages';
        this.loading = false;
        this.cdr.detectChanges();
        console.error(err);
      }
    });
  }

  get isStagiaire(): boolean {
    const user = this.authService.getCurrentUser();
    return user?.role === 'STAGIAIRE';
  }

  postuler(stage: Stage): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.snackBar.open('Vous devez être connectée en tant que stagiaire pour postuler.', 'Fermer', { duration: 3000 });
      return;
    }

    this.candidatureService.postuler({
      stagiaireId: user.profilId,
      stageId: stage.id
    }).subscribe({
      next: () => {
        this.snackBar.open('Candidature envoyée avec succès !', 'Fermer', { duration: 3000 });
      },
      error: (err) => {
        const message = err.error?.message || 'Erreur lors de l\'envoi de la candidature';
        this.snackBar.open(message, 'Fermer', { duration: 4000 });
        console.error(err);
      }
    });
  }
}
