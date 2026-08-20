import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import { StageService } from '../../services/stage';
import { Stage } from '../../models/stage.model';

@Component({
  selector: 'app-stage-form',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatSelectModule, MatDatepickerModule, MatNativeDateModule, TranslatePipe
  ],
  templateUrl: './stage-form.html',
  styleUrl: './stage-form.css'
})
export class StageForm implements OnInit {
  stage: Partial<Stage> = {
    titre: '',
    description: '',
    service: '',
    dateDebut: '',
    dateFin: '',
    typeStage: 'PFE',
    nbPlaces: 1
  };
  dateDebutModel: Date | null = null;
  dateFinModel: Date | null = null;
  minDate: Date = new Date(); // Date minimum : aujourd'hui

  error = '';
  success = '';
  loading = false;

  constructor(private stageService: StageService, private router: Router) {}

  ngOnInit(): void {
    // S'assurer que minDate est bien d'aujourd'hui
    this.minDate = new Date();
    this.minDate.setHours(0, 0, 0, 0);
  }

  onSubmit(): void {
    this.error = '';
    this.success = '';
    this.loading = true;

    const payload: Partial<Stage> = {
      ...this.stage,
      dateDebut: this.formatDate(this.dateDebutModel),
      dateFin: this.formatDate(this.dateFinModel)
    };

    this.stageService.createStage(payload).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'STAGE_FORM.SUCCESS';
        setTimeout(() => void this.router.navigate(['/stages']), 1200);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'STAGE_FORM.ERROR';
        console.error(err);
      }
    });
  }

  private formatDate(date: Date | null): string {
    if (!date) return '';
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
