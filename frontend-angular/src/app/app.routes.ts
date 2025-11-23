import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./login/login').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register').then(m => m.RegisterComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./profile/profile').then(m => m.ProfileComponent)
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
