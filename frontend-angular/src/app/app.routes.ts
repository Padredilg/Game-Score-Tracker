import { Routes } from '@angular/router';
import { JoinGroupComponent } from './join-group/join-group.component';

export const routes: Routes = [
  { path: '', redirectTo: 'join-group', pathMatch: 'full' },
  { path: 'join-group', component: JoinGroupComponent },
];
