import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { NotificationService } from '../services/notification';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class NotificationInterceptor implements HttpInterceptor {
  private successMessages: { [key: string]: string } = {
    'POST:/api/stages': 'STAGES.SUCCESS_CREATE',
    'PUT:/api/stages': 'STAGES.SUCCESS_UPDATE',
    'DELETE:/api/stages': 'STAGES.SUCCESS_DELETE',
    'PATCH:/api/stages': 'STAGES.SUCCESS_UPDATE',
    'POST:/api/candidatures': 'CANDIDATURES.SUCCESS_POSTULER',
    'DELETE:/api/candidatures': 'CANDIDATURES.SUCCESS_ANNULER',
    'PATCH:/api/candidatures/accepter': 'CANDIDATURES.SUCCESS_ACCEPTER',
    'PATCH:/api/candidatures/refuser': 'CANDIDATURES.SUCCESS_REFUSER',
    'POST:/api/affectations': 'CANDIDATURES.SUCCESS_AFFECTER',
    'PATCH:/api/affectations/reaffecter': 'AFFECTATIONS.SUCCESS_REAFFECTER',
    'DELETE:/api/affectations': 'AFFECTATIONS.SUCCESS_DELETE',
    'POST:/api/rapports': 'RAPPORTS.SUCCESS_DEPOSER',
    'PUT:/api/rapports': 'RAPPORTS.SUCCESS_RESOUMETTRE',
    'PATCH:/api/rapports/valider': 'RAPPORTS.SUCCESS_VALIDER',
    'PATCH:/api/rapports/rejeter': 'RAPPORTS.SUCCESS_REJETER'
  };

  constructor(
    private notificationService: NotificationService,
    private translate: TranslateService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          // Chercher un message de succès correspondant
          const key = `${req.method}:${req.url}`;
          for (const [pattern, messageKey] of Object.entries(this.successMessages)) {
            if (key.includes(pattern.split(':')[1])) {
              const method = pattern.split(':')[0];
              if (req.method === method && req.url.includes(pattern.split(':')[1])) {
                const message = this.translate.instant(messageKey);
                if (message && !message.includes('CANDIDATURES') && !message.includes('STAGES')) {
                  this.notificationService.success(message);
                }
              }
            }
          }
        }
      }),
      catchError(err => {
        if (err instanceof HttpErrorResponse) {
          let errorMessage = 'Une erreur est survenue';

          if (err.error?.message) {
            errorMessage = err.error.message;
          } else if (err.status === 0) {
            errorMessage = 'Le serveur n\'est pas accessible';
          } else if (err.status === 401) {
            errorMessage = 'Session expirée. Reconnexion requise.';
          } else if (err.status === 403) {
            errorMessage = 'Accès refusé';
          } else if (err.status === 404) {
            errorMessage = 'Ressource non trouvée';
          } else if (err.status === 400) {
            errorMessage = err.error?.message || 'Requête invalide';
          }

          this.notificationService.error(errorMessage);
        }
        return throwError(() => err);
      })
    );
  }
}
