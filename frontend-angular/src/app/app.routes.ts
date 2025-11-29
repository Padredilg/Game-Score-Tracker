import { Routes } from '@angular/router';
import { JoinGroupComponent } from './join-group/join-group.component';

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
  {
    path: 'main-dashboard',
    loadComponent: () => import('./main-dashboard/main-dashboard').then(m => m.MainDashboard)
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'join-group', component: JoinGroupComponent },
  { path: '**', redirectTo: 'login' }
];
