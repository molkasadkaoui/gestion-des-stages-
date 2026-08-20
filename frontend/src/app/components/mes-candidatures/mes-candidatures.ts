import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Candidature } from '../../models/candidature.model';
import { CandidatureService } from '../../services/candidature';
import { AuthService } from '../../services/auth';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-mes-candidatures',
  standalone: true,
  imports: [CommonModule, RouterLink, MatTableModule, MatChipsModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, TranslatePipe],
  templateUrl: './mes-candidatures.html',
  styleUrl: './mes-candidatures.css'
})
export class MesCandidatures implements OnInit {
  candidatures: Candidature[] = [];
  loading = true;
  displayedColumns: string[] = ['stageTitre', 'dateCandidature', 'statut', 'actions'];

  constructor(
    private candidatureService: CandidatureService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.loadCandidatures();
  }

  loadCandidatures(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.loading = false;
      return;
    }

    this.candidatureService.getCandidaturesByStagiaire(user.profilId, 0, 100).subscribe({
      next: (data) => {
        this.candidatures = data.content;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  annuler(candidature: Candidature): void {
    if (!confirm(this.translate.instant('CANDIDATURES.CONFIRM_ANNULER'))) {
      return;
    }

    this.candidatureService.annuler(candidature.id).subscribe({
      next: () => {
        this.notificationService.success(this.translate.instant('CANDIDATURES.SUCCESS_ANNULER'));
        this.loadCandidatures();
      },
      error: (err) => {
        this.notificationService.error(err.error?.message || 'Erreur lors de l\'annulation');
      }
    });
  }

  canAnnuler(candidature: Candidature): boolean {
    return candidature.statut === 'EN_ATTENTE';
  }

  statutKey(statut: string): string {
    return 'CANDIDATURES.STATUT_' + statut;
  }
}
