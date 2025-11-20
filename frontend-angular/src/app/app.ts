import { Component } from '@angular/core';
import { PlayerLogin } from './player-login/player-login';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PlayerLogin],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}
