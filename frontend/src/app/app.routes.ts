import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

// Composants pages
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { StageList } from './components/stage-list/stage-list';
import { Dashboard } from './components/dashboard/dashboard';
import { UserManagement } from './components/user-management/user-management';
import { CandidatureList } from './components/candidature-list/candidature-list';
import { MesCandidatures } from './components/mes-candidatures/mes-candidatures';
import { MonAffectation } from './components/mon-affectation/mon-affectation';
import { RapportForm } from './components/rapport-form/rapport-form';
import { MesStagesEncadrant } from './components/mes-stages-encadrant/mes-stages-encadrant';

// Nouveaux composants pour Task 5
import { NotificationsPanel } from './components/notifications-panel/notifications-panel';
import { RapportsEncadrant } from './components/rapports-encadrant/rapports-encadrant';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  
  // Routes protégées
  { path: 'stages', component: StageList, canActivate: [authGuard] },
  
  // Routes STAGIAIRE
  { path: 'mes-candidatures', component: MesCandidatures, canActivate: [authGuard] },
  { path: 'mon-affectation', component: MonAffectation, canActivate: [authGuard] },
  { path: 'rapports/nouveau', component: RapportForm, canActivate: [authGuard] },
  
  // Routes ENCADRANT
  { path: 'mes-stages', component: MesStagesEncadrant, canActivate: [authGuard] },
  { path: 'mes-rapports', component: RapportsEncadrant, canActivate: [authGuard] },
  
  // Routes ADMIN
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'utilisateurs', component: UserManagement, canActivate: [authGuard] },
  { path: 'candidatures', component: CandidatureList, canActivate: [authGuard] },
  
  // Route Notifications (accessible à tous les rôles)
  { path: 'notifications', component: NotificationsPanel, canActivate: [authGuard] },
  
  // Route wildcard
  { path: '**', redirectTo: '/' }
];
