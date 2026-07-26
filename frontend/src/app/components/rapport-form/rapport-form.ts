import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { RapportService } from '../../services/rapport';
import { RapportRequest } from '../../models/rapport.model';

@Component({
  selector: 'app-rapport-form',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './rapport-form.html',
  styleUrl: './rapport-form.css'
})
export class RapportForm {
  request: RapportRequest = { affectationId: 0, fichierUrl: '', commentaire: '' };
  error = '';
  success = '';
  loading = false;

  constructor(private rapportService: RapportService) {}

  onSubmit(): void {
    this.error = '';
    this.success = '';
    this.loading = true;

    this.rapportService.deposer(this.request).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Rapport déposé avec succès !';
        this.request = { affectationId: 0, fichierUrl: '', commentaire: '' };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Erreur lors du dépôt du rapport';
      }
    });
  }
}
