import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Candidature } from '../../models/candidature.model';
import { CandidatureService } from '../../services/candidature';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-mes-candidatures',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatChipsModule, MatProgressSpinnerModule, TranslatePipe],
  templateUrl: './mes-candidatures.html',
  styleUrl: './mes-candidatures.css'
})
export class MesCandidatures implements OnInit {
  candidatures: Candidature[] = [];
  loading = true;
  displayedColumns: string[] = ['stageTitre', 'dateCandidature', 'statut'];

  constructor(
    private candidatureService: CandidatureService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.loading = false;
      return;
    }

    this.candidatureService.getCandidaturesByStagiaire(user.profilId).subscribe({
      next: (data) => {
        this.candidatures = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  statutKey(statut: string): string {
    return 'CANDIDATURES.STATUT_' + statut;
  }
}
