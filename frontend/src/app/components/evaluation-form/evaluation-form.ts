import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { EvaluationService } from '../../services/evaluation';
import { EvaluationRequest } from '../../models/evaluation.model';

@Component({
  selector: 'app-evaluation-form',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, TranslatePipe],
  templateUrl: './evaluation-form.html',
  styleUrl: './evaluation-form.css'
})
export class EvaluationForm {
  request: EvaluationRequest = { affectationId: 0, note: 0, commentaire: '' };
  error = '';
  success = '';
  loading = false;

  constructor(private evaluationService: EvaluationService) {}

  onSubmit(): void {
    this.error = '';
    this.success = '';
    this.loading = true;

    this.evaluationService.evaluer(this.request).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'EVALUATION_FORM.SUCCESS';
        this.request = { affectationId: 0, note: 0, commentaire: '' };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'EVALUATION_FORM.ERROR';
      }
    });
  }
}
