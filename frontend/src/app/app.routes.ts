import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { StageList } from './components/stage-list/stage-list';
import { StageForm } from './components/stage-form/stage-form';
import { CandidatureList } from './components/candidature-list/candidature-list';
import { RapportForm } from './components/rapport-form/rapport-form';
import { EvaluationForm } from './components/evaluation-form/evaluation-form';
import { MesCandidatures } from './components/mes-candidatures/mes-candidatures';
import { MonAffectation } from './components/mon-affectation/mon-affectation';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'stages', component: StageList },
  { path: 'stages/nouveau', component: StageForm },
  { path: 'candidatures', component: CandidatureList },
  { path: 'mes-candidatures', component: MesCandidatures },
  { path: 'mon-affectation', component: MonAffectation },
  { path: 'rapports/nouveau', component: RapportForm },
  { path: 'evaluations/nouveau', component: EvaluationForm },
];
