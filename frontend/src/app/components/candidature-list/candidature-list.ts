import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Candidature } from '../../models/candidature.model';
import { CandidatureService } from '../../services/candidature';
import { AffectationService } from '../../services/affectation';

@Component({
  selector: 'app-candidature-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatChipsModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './candidature-list.html',
  styleUrl: './candidature-list.css'
})
export class CandidatureList implements OnInit {
  candidatures: Candidature[] = [];
  loading = true;
  displayedColumns: string[] = ['stagiaireNom', 'stageTitre', 'dateCandidature', 'statut', 'actions'];

  constructor(
    private candidatureService: CandidatureService,
    private affectationService: AffectationService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.candidatureService.getAll().subscribe({
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

  accepter(c: Candidature): void {
    this.candidatureService.accepter(c.id).subscribe({
      next: () => {
        this.snackBar.open('Candidature acceptée', 'Fermer', { duration: 2500 });
        this.load();
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Erreur', 'Fermer', { duration: 3000 })
    });
  }

  refuser(c: Candidature): void {
    this.candidatureService.refuser(c.id).subscribe({
      next: () => {
        this.snackBar.open('Candidature refusée', 'Fermer', { duration: 2500 });
        this.load();
      },
      error: (err) => this.snackBar.open(err.error?.message || 'Erreur', 'Fermer', { duration: 3000 })
    });
  }

  affecter(c: Candidature): void {
    const encadrantIdStr = prompt('ID de l\'encadrant à affecter :');
    if (!encadrantIdStr) return;

    this.affectationService.affecter({
      candidatureId: c.id,
      encadrantId: Number(encadrantIdStr)
    }).subscribe({
      next: () => this.snackBar.open('Encadrant affecté avec succès', 'Fermer', { duration: 2500 }),
      error: (err) => this.snackBar.open(err.error?.message || 'Erreur lors de l\'affectation', 'Fermer', { duration: 3000 })
    });
  }
}
