import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { UtilisateurService, Utilisateur, UtilisateurUpdateRequest } from '../../../services/utilisateur';
import { NotificationService } from '../../../services/notification';

@Component({
  selector: 'app-user-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    TranslatePipe
  ],
  templateUrl: './user-edit-dialog.component.html',
  styleUrl: './user-edit-dialog.component.css'
})
export class UserEditDialogComponent {
  utilisateur: Utilisateur | null = null;
  formData: UtilisateurUpdateRequest = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    service: '',
    etablissement: '',
    niveauEtude: '',
    filiere: '',
    poste: '',
    role: undefined,
    actif: true
  };
  error = '';
  loading = false;
  isNew = true;
  roles = ['STAGIAIRE', 'ENCADRANT', 'ADMIN'];

  constructor(
    public dialogRef: MatDialogRef<UserEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { utilisateur?: Utilisateur },
    private utilisateurService: UtilisateurService,
    private notificationService: NotificationService
  ) {
    if (data.utilisateur) {
      this.utilisateur = data.utilisateur;
      this.isNew = false;
      this.formData = {
        nom: this.utilisateur.nom,
        prenom: this.utilisateur.prenom,
        email: this.utilisateur.email,
        telephone: this.utilisateur.telephone,
        service: this.utilisateur.service,
        etablissement: this.utilisateur.etablissement,
        niveauEtude: this.utilisateur.niveauEtude,
        filiere: this.utilisateur.filiere,
        poste: this.utilisateur.poste,
        role: this.utilisateur.role,
        actif: this.utilisateur.actif
      };
    }
  }

  onSubmit(): void {
    this.error = '';
    this.loading = true;

    if (this.isNew) {
      // Créer un nouvel utilisateur (non implémenté à cet stade, peut nécessiter endpoint séparé)
      this.notificationService.error('La création d\'utilisateur n\'est pas disponible ici');
      this.loading = false;
    } else if (this.utilisateur) {
      // Mettre à jour
      this.utilisateurService.mettreAJour(this.utilisateur.id, this.formData).subscribe({
        next: () => {
          this.loading = false;
          this.notificationService.success('Utilisateur mis à jour avec succès');
          this.dialogRef.close(true);
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'Erreur lors de la mise à jour';
        }
      });
    }
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  isFieldVisible(field: string): boolean {
    // Afficher les champs selon le rôle
    const role = this.formData.role || this.utilisateur?.role;
    switch (field) {
      case 'telephone':
        return true;
      case 'service':
      case 'poste':
        return role === 'ENCADRANT';
      case 'etablissement':
      case 'niveauEtude':
      case 'filiere':
        return role === 'STAGIAIRE';
      default:
        return true;
    }
  }
}
