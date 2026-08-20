import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { RapportService } from '../../services/rapport';
import { Rapport } from '../../models/rapport.model';
import { RapportDetailDialogComponent } from '../rapport-detail-dialog/rapport-detail-dialog';

@Component({
  selector: 'app-rapports-encadrant',
  standalone: true,
  imports: [
    CommonModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule,
    MatChipsModule, MatMenuModule, MatDialogModule, TranslatePipe
  ],
  templateUrl: './rapports-encadrant.html',
  styleUrl: './rapports-encadrant.css'
})
export class RapportsEncadrant implements OnInit {
  rapports: Rapport[] = [];
  loading = true;

  constructor(
    private rapportService: RapportService,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadRapports();
  }

  loadRapports(): void {
    this.loading = true;
    this.rapportService.getRapportsPourEncadrant(0, 100).subscribe({
      next: (data: any) => {
        this.rapports = data.content;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  verifierDetail(rapport: Rapport): void {
    this.dialog.open(RapportDetailDialogComponent, {
      width: '700px',
      data: rapport
    });
  }

  validerRapport(rapport: Rapport): void {
    if (confirm(this.translate.instant('RAPPORTS.CONFIRM_VALIDER'))) {
      this.rapportService.validerRapport(rapport.id).subscribe({
        next: () => {
          this.translate.get('RAPPORTS.SUCCESS_VALIDER').subscribe((msg: string) => {
            this.cdr.detectChanges();
          });
          this.loadRapports();
        },
        error: (err: any) => {}
      });
    }
  }

  rejeterRapport(rapport: Rapport): void {
    if (confirm(this.translate.instant('RAPPORTS.CONFIRM_REJETER'))) {
      this.rapportService.rejeterRapport(rapport.id).subscribe({
        next: () => {
          this.translate.get('RAPPORTS.SUCCESS_REJETER').subscribe((msg: string) => {
            this.cdr.detectChanges();
          });
          this.loadRapports();
        },
        error: (err: any) => {}
      });
    }
  }

  obtenirCouleurStatut(statut: string): string {
    switch (statut) {
      case 'DEPOSE':
        return '#F59E0B';
      case 'VALIDE':
        return '#22C55E';
      case 'REJETE':
        return '#EF4444';
      default:
        return '#6B7280';
    }
  }

  get rapportsEnAttente(): Rapport[] {
    return this.rapports.filter(r => r.statut === 'DEPOSE');
  }

  get rapportsTraites(): Rapport[] {
    return this.rapports.filter(r => r.statut !== 'DEPOSE');
  }
}
