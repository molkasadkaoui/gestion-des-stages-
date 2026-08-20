import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Candidature } from '../../models/candidature.model';
import { Encadrant } from '../../models/encadrant.model';
import { CandidatureService } from '../../services/candidature';
import { AffectationService } from '../../services/affectation';
import { EncadrantService } from '../../services/encadrant';
import { NotificationService } from '../../services/notification';
import { CandidatureDetailDialogComponent } from '../candidature-detail-dialog/candidature-detail-dialog';

@Component({
  selector: 'app-candidature-list',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatTableModule, MatChipsModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, MatSelectModule, MatFormFieldModule, MatDialogModule,
    TranslatePipe
  ],
  templateUrl: './candidature-list.html',
  styleUrl: './candidature-list.css'
})
export class CandidatureList implements OnInit {
  candidatures: Candidature[] = [];
  encadrants: Encadrant[] = [];
  loading = true;
  affectationsParCandidature: { [key: number]: boolean } = {}; // Map candidatureId -> hasAffectation

  // État du panel d'affectation inline
  affectationEnCours: Candidature | null = null;
  encadrantSelectionne: number | null = null;
  affectationLoading = false;

  constructor(
    private candidatureService: CandidatureService,
    private affectationService: AffectationService,
    private encadrantService: EncadrantService,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private translate: TranslateService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.load();
    this.encadrantService.getAll().subscribe({
      next: (data) => this.encadrants = data,
      error: () => {}
    });
  }

  load(): void {
    this.loading = true;
    this.candidatureService.getAll(0, 100).subscribe({
      next: (data) => { 
        this.candidatures = data.content;
        this.chargerAffectations();
        this.loading = false; 
        this.cdr.detectChanges(); 
      },
      error: () => { this.loading = false; this.cdr.detectChanges(); }
    });
  }

  /**
   * Charger toutes les affectations pour vérifier lesquelles ont une candidature affectée
   */
  chargerAffectations(): void {
    this.affectationService.getAll(0, 1000).subscribe({
      next: (data) => {
        // Réinitialiser la map
        this.affectationsParCandidature = {};
        // Marquer toutes les candidatures qui ont une affectation
        data.content.forEach(aff => {
          this.affectationsParCandidature[aff.candidatureId] = true;
        });
        this.cdr.detectChanges();
      },
      error: () => {} // Silencieusement échouer si pas d'affectations
    });
  }

  /**
   * Vérifier si une candidature a déjà une affectation
   */
  aUneAffectation(candidature: Candidature): boolean {
    return this.affectationsParCandidature[candidature.id] === true;
  }

  statutKey(statut: string): string {
    return 'CANDIDATURES.STATUT_' + statut;
  }

  accepter(c: Candidature): void {
    this.candidatureService.accepter(c.id).subscribe({
      next: () => {
        this.notificationService.success(this.translate.instant('CANDIDATURES.SUCCESS_ACCEPTER'));
        this.load();
      },
      error: (err) => this.notificationService.error(err.error?.message || 'Erreur')
    });
  }

  refuser(c: Candidature): void {
    this.candidatureService.refuser(c.id).subscribe({
      next: () => {
        this.notificationService.success(this.translate.instant('CANDIDATURES.SUCCESS_REFUSER'));
        this.load();
      },
      error: (err) => this.notificationService.error(err.error?.message || 'Erreur')
    });
  }

  ouvrirAffectation(c: Candidature): void {
    this.affectationEnCours = c;
    this.encadrantSelectionne = null;
  }

  annulerAffectation(): void {
    this.affectationEnCours = null;
    this.encadrantSelectionne = null;
  }

  confirmerAffectation(): void {
    if (!this.affectationEnCours || !this.encadrantSelectionne) return;

    this.affectationLoading = true;
    this.affectationService.affecter({
      candidatureId: this.affectationEnCours.id,
      encadrantId: this.encadrantSelectionne
    }).subscribe({
      next: () => {
        this.notificationService.success(this.translate.instant('CANDIDATURES.SUCCESS_AFFECTER'));
        this.affectationLoading = false;
        this.annulerAffectation();
        this.load();
      },
      error: (err) => {
        this.notificationService.error(err.error?.message || this.translate.instant('CANDIDATURES.ERROR_AFFECTER'));
        this.affectationLoading = false;
      }
    });
  }

  /**
   * Ouvrir la modal avec les détails de la candidature
   */
  verifierDetails(candidature: Candidature): void {
    this.dialog.open(CandidatureDetailDialogComponent, {
      width: '700px',
      data: candidature
    });
  }
}
