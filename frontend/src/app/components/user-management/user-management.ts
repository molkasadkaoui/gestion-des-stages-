import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { UtilisateurService, Utilisateur } from '../../services/utilisateur';
import { UserEditDialogComponent } from './user-edit-dialog/user-edit-dialog.component';
import { NotificationService } from '../../services/notification';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatPaginatorModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatTooltipModule,
    TranslatePipe
  ],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css'
})
export class UserManagement implements OnInit {
  utilisateurs: Utilisateur[] = [];
  loading = true;
  error = '';
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  
  // Affichage
  displayedColumns: string[] = ['prenom', 'nom', 'email', 'role', 'approuve', 'actif', 'dateCreation', 'actions'];

  constructor(
    private utilisateurService: UtilisateurService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.chargerUtilisateurs();
  }

  chargerUtilisateurs(page = 0): void {
    this.loading = true;
    this.error = '';
    this.utilisateurService.obtenirTous(page, this.pageSize).subscribe({
      next: (response: any) => {
        this.utilisateurs = response.content || [];
        this.currentPage = response.number || 0;
        this.totalElements = response.totalElements || 0;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Erreur:', err);
        this.error = err.error?.message || 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.chargerUtilisateurs(event.pageIndex);
  }

  ouvrirFormulaire(utilisateur?: Utilisateur): void {
    const dialogRef = this.dialog.open(UserEditDialogComponent, {
      width: '600px',
      data: { utilisateur }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.chargerUtilisateurs(this.currentPage);
      }
    });
  }

  supprimer(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
      this.utilisateurService.supprimer(id).subscribe({
        next: () => {
          this.notificationService.success('Utilisateur supprimé avec succès');
          this.chargerUtilisateurs(this.currentPage);
        },
        error: (err) => {
          this.notificationService.error(err.error?.message || 'Erreur lors de la suppression');
        }
      });
    }
  }

  toggleActif(utilisateur: Utilisateur): void {
    const action = utilisateur.actif ? 'désactiver' : 'activer';
    if (confirm(`Êtes-vous sûr de vouloir ${action} cet utilisateur ?`)) {
      const request = utilisateur.actif
        ? this.utilisateurService.desactiver(utilisateur.id)
        : this.utilisateurService.activer(utilisateur.id);

      request.subscribe({
        next: () => {
          this.notificationService.success(`Utilisateur ${action}é avec succès`);
          this.chargerUtilisateurs(this.currentPage);
        },
        error: (err) => {
          this.notificationService.error(err.error?.message || `Erreur lors de la ${action}ation`);
        }
      });
    }
  }

  approuverEncadrant(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir approuver cet encadrant ?')) {
      this.utilisateurService.appouverEncadrant(id).subscribe({
        next: () => {
          this.notificationService.success('Encadrant approuvé avec succès');
          this.chargerUtilisateurs(this.currentPage);
        },
        error: (err) => {
          this.notificationService.error(err.error?.message || 'Erreur lors de l\'approbation');
        }
      });
    }
  }

  rejeterEncadrant(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir rejeter cet encadrant ?')) {
      this.utilisateurService.rejeterEncadrant(id).subscribe({
        next: () => {
          this.notificationService.success('Encadrant rejeté avec succès');
          this.chargerUtilisateurs(this.currentPage);
        },
        error: (err) => {
          this.notificationService.error(err.error?.message || 'Erreur lors du rejet');
        }
      });
    }
  }

  getRoleColor(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'warn';
      case 'ENCADRANT':
        return 'accent';
      case 'STAGIAIRE':
        return 'primary';
      default:
        return '';
    }
  }
}
