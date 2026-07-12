import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Stage } from '../../models/stage.model';
import { StageService } from '../../services/stage';

@Component({
  selector: 'app-stage-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatChipsModule, MatProgressSpinnerModule],
  templateUrl: './stage-list.html',
  styleUrl: './stage-list.css'
})
export class StageList implements OnInit {
  stages: Stage[] = [];
  loading = true;
  error = '';
  displayedColumns: string[] = ['titre', 'service', 'typeStage', 'statut', 'nbPlaces', 'dateDebut', 'dateFin'];

  constructor(
    private stageService: StageService,
    private cdr: ChangeDetectorRef
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
}
