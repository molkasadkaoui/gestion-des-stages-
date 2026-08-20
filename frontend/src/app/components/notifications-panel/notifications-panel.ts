import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { TranslatePipe } from '@ngx-translate/core';
import { NotificationSystemService } from '../../services/notification-system';
import { Notification } from '../../models/notification.model';
import { Subject, interval } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-notifications-panel',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule, MatTabsModule, TranslatePipe],
  templateUrl: './notifications-panel.html',
  styleUrl: './notifications-panel.css'
})
export class NotificationsPanel implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  loading = false;
  private destroy$ = new Subject<void>();

  constructor(
    private notificationService: NotificationSystemService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    // Recharger toutes les 30 secondes
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.loadNotifications());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadNotifications(): void {
    this.loading = true;
    this.notificationService.getAll(0, 100).subscribe({
      next: (data: any) => {
        this.notifications = data.content;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  marquerCommeLue(notification: Notification): void {
    this.notificationService.marquerCommeLue(notification.id).subscribe({
      next: () => {
        notification.lu = true;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  marquerToutCommeLu(): void {
    this.notificationService.marquerToutCommeLu().subscribe({
      next: () => {
        this.notifications.forEach(n => n.lu = true);
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  obtenirCouleurType(type: string): string {
    switch (type) {
      case 'CANDIDATURE':
        return '#3B82F6';
      case 'AFFECTATION':
        return '#22C55E';
      case 'RAPPORT':
        return '#F59E0B';
      default:
        return '#6B7280';
    }
  }

  obtenirIconeType(type: string): string {
    switch (type) {
      case 'CANDIDATURE':
        return 'assignment';
      case 'AFFECTATION':
        return 'person_add';
      case 'RAPPORT':
        return 'description';
      default:
        return 'notifications';
    }
  }

  get nonLues(): Notification[] {
    return this.notifications.filter(n => !n.lu);
  }

  get lues(): Notification[] {
    return this.notifications.filter(n => n.lu);
  }
}
