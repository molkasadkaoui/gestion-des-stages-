import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const user = authService.getCurrentUser();

    if (user && allowedRoles.includes(user.role)) {
      return true;
    }

    if (!authService.isLoggedIn()) {
      router.navigate(['/login']);
    } else {
      router.navigate(['/']);
    }
    return false;
  };
};
