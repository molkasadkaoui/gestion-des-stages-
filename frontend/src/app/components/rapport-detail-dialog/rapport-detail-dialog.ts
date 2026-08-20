import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { Rapport } from '../../models/rapport.model';
import { FileService } from '../../services/file';

@Component({
  selector: 'app-rapport-detail-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatIconModule, MatTabsModule, MatButtonModule, TranslatePipe],
  templateUrl: './rapport-detail-dialog.html',
  styleUrl: './rapport-detail-dialog.css'
})
export class RapportDetailDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public rapport: Rapport,
    private fileService: FileService
  ) {}

  telechargerRapport(): void {
    // Si le rapport a un fichierUrl, télécharger le fichier
    if (this.rapport.fichierUrl) {
      window.open(this.rapport.fichierUrl, '_blank');
    }
  }

  obtenirNomFichier(): string {
    if (!this.rapport.fichierUrl) return '';
    return this.rapport.fichierUrl.substring(this.rapport.fichierUrl.lastIndexOf('/') + 1);
  }
}
