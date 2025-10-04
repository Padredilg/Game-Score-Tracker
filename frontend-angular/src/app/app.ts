import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LogIn } from './log-in/log-in/log-in';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LogIn],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('Game Score Tracker');
}
