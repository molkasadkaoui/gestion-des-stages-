import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { TranslatePipe } from '@ngx-translate/core';
import { Stage } from '../../models/stage.model';
import { StageService, PageResponse } from '../../services/stage';

@Component({
  selector: 'app-mes-stages-encadrant',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    TranslatePipe
  ],
  templateUrl: './mes-stages-encadrant.html',
  styleUrl: './mes-stages-encadrant.css'
})
export class MesStagesEncadrant implements OnInit {
  stages: Stage[] = [];
  loading = true;
  error = '';
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(
    private stageService: StageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStages();
  }

  loadStages(page = 0): void {
    this.loading = true;
    this.stageService.getMesStages(page, this.pageSize).subscribe({
      next: (response: PageResponse<Stage>) => {
        this.stages = response.content;
        this.currentPage = response.number;
        this.totalElements = response.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de vos stages';
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

  typeKey(type: string): string {
    return 'STAGES.TYPE_' + type;
  }

  statutKey(statut: string): string {
    return 'STAGES.STATUT_' + statut;
  }
}
