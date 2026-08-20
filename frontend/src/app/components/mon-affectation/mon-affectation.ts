import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Affectation } from '../../models/affectation.model';
import { AffectationService } from '../../services/affectation';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-mon-affectation',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule, TranslatePipe],
  templateUrl: './mon-affectation.html',
  styleUrl: './mon-affectation.css'
})
export class MonAffectation implements OnInit {
  affectations: Affectation[] = [];
  loading = true;

  constructor(
    private affectationService: AffectationService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.profilId) {
      this.loading = false;
      return;
    }

    this.affectationService.getByStagiaire(user.profilId, 0, 100).subscribe({
      next: (data) => {
        this.affectations = data.content;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
