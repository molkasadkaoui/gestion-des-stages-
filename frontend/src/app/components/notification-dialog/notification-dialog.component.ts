import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface NotificationData {
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration: number;
}

@Component({
  selector: 'app-notification-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <div class="notification-dialog" [class]="'notification-' + data.type">
      <div class="notification-content">
        <mat-icon class="notification-icon">{{ getIcon() }}</mat-icon>
        <div class="notification-message">{{ data.message }}</div>
        <button mat-icon-button (click)="close()" class="notification-close">
          <mat-icon>close</mat-icon>
        </button>
      </div>
      <div class="notification-progress" [style.animation-duration.ms]="data.duration"></div>
    </div>
  `,
  styles: [`
    .notification-dialog {
      padding: 16px;
      border-radius: 12px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      position: relative;
      overflow: hidden;
      min-width: 300px;
    }

    .notification-content {
      display: flex;
      align-items: center;
      gap: 12px;
      position: relative;
      z-index: 2;
    }

    .notification-icon {
      font-size: 24px;
      width: 24px;
      height: 24px;
      flex-shrink: 0;
    }

    .notification-message {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      line-height: 1.4;
    }

    .notification-close {
      flex-shrink: 0;
      margin-left: auto;
    }

    .notification-progress {
      position: absolute;
      bottom: 0;
      left: 0;
      height: 3px;
      animation: progress linear forwards;
    }

    @keyframes progress {
      from { width: 100%; }
      to { width: 0; }
    }

    /* Success */
    .notification-success {
      background: #f0fdf4;
      border-left: 4px solid #22c55e;
    }

    .notification-success .notification-icon {
      color: #22c55e;
    }

    .notification-success .notification-message {
      color: #166534;
    }

    .notification-success .notification-progress {
      background: #22c55e;
    }

    /* Error */
    .notification-error {
      background: #fef2f2;
      border-left: 4px solid #ef4444;
    }

    .notification-error .notification-icon {
      color: #ef4444;
    }

    .notification-error .notification-message {
      color: #b91c1c;
    }

    .notification-error .notification-progress {
      background: #ef4444;
    }

    /* Info */
    .notification-info {
      background: #f0f9ff;
      border-left: 4px solid #3b82f6;
    }

    .notification-info .notification-icon {
      color: #3b82f6;
    }

    .notification-info .notification-message {
      color: #1e40af;
    }

    .notification-info .notification-progress {
      background: #3b82f6;
    }

    /* Warning */
    .notification-warning {
      background: #fffbeb;
      border-left: 4px solid #f59e0b;
    }

    .notification-warning .notification-icon {
      color: #f59e0b;
    }

    .notification-warning .notification-message {
      color: #b45309;
    }

    .notification-warning .notification-progress {
      background: #f59e0b;
    }
  `]
})
export class NotificationDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: NotificationData,
    private dialogRef: MatDialogRef<NotificationDialogComponent>
  ) {}

  getIcon(): string {
    switch (this.data.type) {
      case 'success':
        return 'check_circle';
      case 'error':
        return 'error';
      case 'warning':
        return 'warning';
      case 'info':
      default:
        return 'info';
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
