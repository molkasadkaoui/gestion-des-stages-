import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Stage } from '../../models/stage.model';
import { StageService } from '../../services/stage';
import { CandidatureService } from '../../services/candidature';
import { AuthService } from '../../services/auth';
import { NotificationService } from '../../services/notification';
import { FileService } from '../../services/file';
import { CandidatureFormComponent } from '../candidature-form/candidature-form';

@Component({
  selector: 'app-stage-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink, FormsModule,
    MatChipsModule, MatProgressSpinnerModule, MatButtonModule,
    MatIconModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatPaginatorModule, MatTooltipModule,
    MatDialogModule,
    TranslatePipe
  ],
  templateUrl: './stage-list.html',
  styleUrl: './stage-list.css'
})
export class StageList implements OnInit {
  stages: Stage[] = [];
  loading = true;
  error = '';
  searchText = '';
  filterType = '';
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Pour tracker les candidatures en cours
  candidatureInProgress = new Set<number>();

  constructor(
    private stageService: StageService,
    private candidatureService: CandidatureService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService,
    private translate: TranslateService,
    private fileService: FileService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadStages();
  }

  loadStages(page = 0): void {
    this.loading = true;
    this.stageService.getStages({}, page, this.pageSize).subscribe({
      next: (response) => {
        this.stages = response.content;
        this.currentPage = response.number;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
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

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.loadStages(event.pageIndex);
  }

  get filteredStages(): Stage[] {
    return this.stages.filter(s => {
      const matchText = !this.searchText ||
        s.titre.toLowerCase().includes(this.searchText.toLowerCase()) ||
        s.service.toLowerCase().includes(this.searchText.toLowerCase());
      const matchType = !this.filterType || s.typeStage === this.filterType;
      return matchText && matchType;
    });
  }

  get isStagiaire(): boolean {
    return this.authService.getCurrentUser()?.role === 'STAGIAIRE';
  }

  get isAdmin(): boolean {
    return this.authService.getCurrentUser()?.role === 'ADMIN';
  }

  /**
   * Vérifier si un stage peut être postulé
   */
  canPostuler(stage: Stage): boolean {
    if (stage.statut !== 'OUVERT') return false;

    // Vérifier si la date de début est passée
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const stageStartDate = new Date(stage.dateDebut);
    stageStartDate.setHours(0, 0, 0, 0);
    
    if (stageStartDate < today) return false;

    // Note: La vérification des places saturées devrait être faite côté backend
    // Pour maintenant, on suppose que c'est disponible
    return true;
  }

  /**
   * Raison pour laquelle on ne peut pas postuler
   */
  getPostulerDisabledReason(stage: Stage): string {
    if (stage.statut !== 'OUVERT') return 'Stage fermé';

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const stageStartDate = new Date(stage.dateDebut);
    stageStartDate.setHours(0, 0, 0, 0);
    
    if (stageStartDate < today) return 'Stage déjà commencé';

    return '';
  }

  typeKey(type: string): string { return 'STAGES.TYPE_' + type; }
  statutKey(statut: string): string { return 'STAGES.STATUT_' + statut; }

  /**
   * Ouvrir le formulaire de candidature
   */
  ouvrirFormulaireCandidature(stage: Stage): void {
    const user = this.authService.getCurrentUser();
    if (!user || user.role !== 'STAGIAIRE') {
      this.notificationService.warning('Vous devez être connecté en tant que stagiaire pour postuler.');
      return;
    }

    if (!this.canPostuler(stage)) {
      this.notificationService.error(this.getPostulerDisabledReason(stage));
      return;
    }

    const dialogRef = this.dialog.open(CandidatureFormComponent, {
      width: '600px',
      data: { stage, stagiaire: user }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadStages();
      }
    });
  }

  fermer(stage: Stage): void {
    this.stageService.fermer(stage.id).subscribe({
      next: () => { 
        this.notificationService.success('Stage fermé'); 
        this.loadStages(); 
      },
      error: (err) => this.notificationService.error(err.error?.message || 'Erreur')
    });
  }

  annuler(stage: Stage): void {
    this.stageService.annuler(stage.id).subscribe({
      next: () => { 
        this.notificationService.success('Stage annulé'); 
        this.loadStages(); 
      },
      error: (err) => this.notificationService.error(err.error?.message || 'Erreur')
    });
  }

  rouvrir(stage: Stage): void {
    this.stageService.rouvrir(stage.id).subscribe({
      next: () => { 
        this.notificationService.success('Stage rouvert'); 
        this.loadStages(); 
      },
      error: (err) => this.notificationService.error(err.error?.message || 'Erreur')
    });
  }
}
