import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { RapportService } from '../../services/rapport';
import { AffectationService } from '../../services/affectation';
import { AuthService } from '../../services/auth';
import { RapportRequest } from '../../models/rapport.model';
import { Affectation } from '../../models/affectation.model';

@Component({
  selector: 'app-rapport-form',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatProgressSpinnerModule, TranslatePipe],
  templateUrl: './rapport-form.html',
  styleUrl: './rapport-form.css'
})
export class RapportForm implements OnInit {
  affectation: Affectation | null = null;
  checkingAffectation = true;

  request: Omit<RapportRequest, 'affectationId'> = { fichierUrl: '', commentaire: '' };
  error = '';
  success = '';
  loading = false;

  constructor(
    private rapportService: RapportService,
    private affectationService: AffectationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.checkingAffectation = false;
      return;
    }

    this.affectationService.getByStagiaire(user.profilId).subscribe({
      next: (data) => {
        this.affectation = data.length > 0 ? data[0] : null;
        this.checkingAffectation = false;
      },
      error: () => {
        this.checkingAffectation = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.affectation) return;

    this.error = '';
    this.success = '';
    this.loading = true;

    this.rapportService.deposer({
      affectationId: this.affectation.id,
      fichierUrl: this.request.fichierUrl,
      commentaire: this.request.commentaire
    }).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'RAPPORT_FORM.SUCCESS';
        this.request = { fichierUrl: '', commentaire: '' };
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'RAPPORT_FORM.ERROR';
      }
    });
  }
}
