import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { Stats } from '../../models/stats.model';
import { StatsService } from '../../services/stats';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressSpinnerModule, MatButtonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  stats: Stats | null = null;
  loading = true;
  error = '';

  constructor(
    private statsService: StatsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.statsService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Stats error:', err);
        if (err.status === 403 || err.status === 401) {
          this.error = 'Session expirée ou droits insuffisants. Veuillez vous reconnecter.';
        } else if (err.status === 0) {
          this.error = 'Serveur backend inaccessible. Vérifiez que Spring Boot est démarré sur le port 8081.';
        } else {
          this.error = `Erreur ${err.status} : ${err.error?.message || 'Impossible de charger les statistiques.'}`;
        }
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
