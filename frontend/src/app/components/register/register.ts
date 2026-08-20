import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../services/auth';
import { RegisterRequest } from '../../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatSelectModule, MatProgressSpinnerModule, TranslatePipe
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  request: RegisterRequest = {
    nom: '', prenom: '', email: '', motDePasse: '', role: 'STAGIAIRE',
    etablissement: '', niveauEtude: '', filiere: '', telephone: '',
    service: '', poste: ''
  };
  error = '';
  loading = false;
  showPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.error = '';
    this.loading = true;

    this.authService.register(this.request).subscribe({
      next: (response) => {
        this.loading = false;
        // Si c'est un encadrant sans token, afficher un message spécial
        if (this.request.role === 'ENCADRANT' && !response.token) {
          this.error = 'ENCADRANT_AWAITING_APPROVAL';
          setTimeout(() => {
            void this.router.navigate(['/login']);
          }, 3000);
        } else {
          void this.router.navigate(['/stages']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'REGISTER.ERROR';
        console.error(err);
      }
    });
  }
}
