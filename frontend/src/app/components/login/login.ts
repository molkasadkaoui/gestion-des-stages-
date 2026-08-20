import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../services/auth';
import { NotificationService } from '../../services/notification';
import { LoginRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatProgressSpinnerModule, TranslatePipe
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  credentials: LoginRequest = { email: '', motDePasse: '' };
  error = '';
  loading = false;
  showPassword = false;

  constructor(
    private authService: AuthService,
    private notification: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(): void {
    this.error = '';
    this.loading = true;
    this.cdr.detectChanges();

    this.authService.login(this.credentials).subscribe({
      next: (user) => {
        this.loading = false;
        this.notification.success(`Bienvenue ${user.prenom} ${user.nom} !`);
        void this.router.navigate(['/stages']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err.error?.message || 'Email ou mot de passe incorrect.';
        this.error = msg;
        this.notification.error(msg);
        this.cdr.detectChanges();
      }
    });
  }
}
