import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Activity {
  result: string;
  score: string;
  when: string;
}

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent {
  username = 'Julian';
  handle = '@julian';
  role = 'Player';
  status = 'Active';
  memberSince = 'Member Since October 2024';

  gamesPlayed = 47;
  totalWins = 32;
  winRate = 68;

  activities: Activity[] = [
    { result: 'Won against Team Alpha', score: 'Score: 25–18', when: '2 days ago' },
    { result: 'Lost to Team Beta', score: 'Score: 22–25', when: '5 days ago' },
    { result: 'Won against Team Gamma', score: 'Score: 25–20', when: '1 week ago' },
  ];

  onEditProfilePhoto() {
    alert('Edit profile photo clicked (hook up later).');
  }

  onChangePassword() {
    alert('Change password clicked (hook up to backend).');
  }

  onSignOut() {
    alert('Sign out clicked (navigate to login when ready).');
  }

  onBackToDashboard() {
    alert('BACK TO DASHBOARD clicked (hook up routing later).');
  }
}
