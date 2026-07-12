import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { StageList } from './components/stage-list/stage-list';
import { StageForm } from './components/stage-form/stage-form';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'stages', component: StageList },
  { path: 'stages/nouveau', component: StageForm },
];
