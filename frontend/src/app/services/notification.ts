import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NotificationDialogComponent } from '../components/notification-dialog/notification-dialog.component';

export type NotificationType = 'success' | 'error' | 'info' | 'warning';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private dialog: MatDialog) {}

  show(message: string, type: NotificationType = 'info', duration = 4000): void {
    this.dialog.open(NotificationDialogComponent, {
      width: '400px',
      panelClass: `notification-${type}`,
      data: { message, type, duration },
      disableClose: false,
      backdropClass: 'notification-backdrop'
    });

    // Auto close après duration
    if (duration > 0) {
      setTimeout(() => {
        this.dialog.closeAll();
      }, duration);
    }
  }

  success(message: string, duration = 3000): void {
    this.show(message, 'success', duration);
  }

  error(message: string, duration = 5000): void {
    this.show(message, 'error', duration);
  }

  info(message: string, duration = 4000): void {
    this.show(message, 'info', duration);
  }

  warning(message: string, duration = 4000): void {
    this.show(message, 'warning', duration);
  }
}
