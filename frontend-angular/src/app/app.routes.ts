import { Routes } from '@angular/router';
import { JoinGroupComponent } from './join-group/join-group.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [

  {
    path: 'login',
    loadComponent: () => import('./login/login').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./register/register').then((m) => m.RegisterComponent)
  },

  {
    path: 'profile',
    loadComponent: () => import('./profile/profile').then((m) => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'profile/:userId',
    loadComponent: () => import('./profile/profile').then((m) => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  
  {
    path: 'main-dashboard',
    loadComponent: () => import('./main-dashboard/main-dashboard').then((m) => m.MainDashboard),
    canActivate: [AuthGuard]
  },
  { path: 'join-group', 
    component: JoinGroupComponent,
    canActivate: [AuthGuard]
  },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];
