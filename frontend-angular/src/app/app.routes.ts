import { Routes } from '@angular/router';
import { JoinGroupComponent } from './join-group/join-group.component';

export const routes: Routes = [
  { path: '', redirectTo: 'join-group', pathMatch: 'full' },
  { path: 'join-group', component: JoinGroupComponent },
  {
    path: 'login',
    // ⬇️ exact path, no spaces, all lowercase
    loadComponent: () => import('./login/login').then(m => m.LoginComponent)
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
