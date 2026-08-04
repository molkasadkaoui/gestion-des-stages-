import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Stage } from '../../models/stage.model';
import { StageService } from '../../services/stage';
import { CandidatureService } from '../../services/candidature';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-stage-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, MatTableModule, MatChipsModule,
    MatProgressSpinnerModule, MatButtonModule, TranslatePipe
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
    private snackBar: MatSnackBar,
    private translate: TranslateService
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

  get isAdmin(): boolean {
    const user = this.authService.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  typeKey(type: string): string {
    return 'STAGES.TYPE_' + type;
  }

  statutKey(statut: string): string {
    return 'STAGES.STATUT_' + statut;
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
        this.snackBar.open(this.translate.instant('STAGES.SUCCESS_POSTULER'), 'Fermer', { duration: 3000 });
      },
      error: (err) => {
        const message = err.error?.message || 'Erreur lors de l\'envoi de la candidature';
        this.snackBar.open(message, 'Fermer', { duration: 4000 });
        console.error(err);
      }
    });
  }
}
