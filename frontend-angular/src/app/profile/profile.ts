import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GroupService } from '../services/group.service';
import { MatchesService } from '../services/matches.service';
import { UserService } from '../services/user.service';
import { MatIcon } from '@angular/material/icon';
import { StoredUser } from '../models/stored-user.model';
import { Activity } from '../models/activity-model';

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [CommonModule, MatIcon],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class ProfileComponent {
  private groupService = inject(GroupService);
  private matchesService = inject(MatchesService);
  private userService = inject(UserService);
  username = '';
  handle = '';
  role = '';
  status = 'Active';
  memberSince = 'Member Since 2025';
  groupName = '';

  gamesPlayed = 0;
  totalWins = 0;
  winRate = 0;

  activities: Activity[] = [];

  constructor(private router: Router) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u: StoredUser = JSON.parse(raw);
        this.username = u.username;
        this.handle = '@' + u.username;
        this.role = u.role;
      }
    } catch {}
  }

  ngOnInit() {
    let currentUserId: string | null = null;
    try {
      const raw = localStorage.getItem('user');
      const u: StoredUser | null = raw ? JSON.parse(raw) : null;
      currentUserId = u?.userId || null;
    } catch {}
    if (currentUserId) {
      this.userService.getUser(currentUserId).subscribe({
        next: (u: any) => {
          this.gamesPlayed = u.matchesPlayed || 0;
          this.totalWins = u.victories || 0;
          const wp =
            u.winPercentage != null
              ? Math.round(u.winPercentage * 100)
              : this.gamesPlayed
                ? Math.round((this.totalWins / this.gamesPlayed) * 100)
                : 0;
          this.winRate = wp;
        },
      });
    }
    this.groupService.getGroupDetails().subscribe({
      next: (resp: any) => {
        this.groupName = resp?.group?.groupName || '';
        const members = resp?.members || [];
        const me = currentUserId
          ? members.find((m: any) => m.userId === currentUserId)
          : members.find((m: any) => m.username === this.username);
        if (me?.userId) {
          this.matchesService.getUserMatches(me.userId).subscribe({
            next: (dto: any) => {
              const items = dto?.matches || [];
              this.activities = items.slice(0, 10).map((m: any) => ({
                result: m.result,
                score: `Score: ${m.myScore ?? ''}`.trim(),
                when: new Date(m.matchDate).toLocaleDateString(),
              }));
            },
          });
        }
      },
      error: (err) => console.log(err),
    });
  }

  onEditProfilePhoto() {
    alert('Edit profile photo TBD');
  }
  onChangePassword() {
    alert('Change password TBD');
  }
  onSignOut() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
  onBackToDashboard() {
    this.router.navigate(['/main-dashboard']);
  }
}
