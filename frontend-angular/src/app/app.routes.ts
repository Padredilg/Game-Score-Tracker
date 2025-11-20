import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    // ⬇️ exact path, no spaces, all lowercase
    loadComponent: () => import('./login/login').then(m => m.LoginComponent)
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
