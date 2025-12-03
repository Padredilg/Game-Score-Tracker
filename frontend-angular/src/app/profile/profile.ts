import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GroupService } from '../services/group.service';
import { ActivatedRoute } from '@angular/router';
import { MatchesService } from '../services/matches.service';
import { UserService } from '../services/user.service';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { StoredUser } from '../models/stored-user.model';
import { Activity } from '../models/activity-model';

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [CommonModule, MatIcon, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class ProfileComponent {
  private groupService = inject(GroupService);
  private matchesService = inject(MatchesService);
  private userService = inject(UserService);
  username = '';
  nickname = '';
  userId: string | null = null;
  handle = '';
  role = '';
  status = 'Active';
  memberSince = 'Member Since 2025';
  groupName = '';

  gamesPlayed = 0;
  totalWins = 0;
  winRate = 0;

  activities: Activity[] = [];
  isOwnProfile = true;
  isAdmin = false;
  showDeleteModal = false;
  deleteContext: { text: string; matchId?: string } | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
  ) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u: StoredUser = JSON.parse(raw);
        this.username = u.username;
        this.nickname = (u as any).nickname || u.username;
        this.userId = (u as any).userId || null;
        this.handle = '@' + this.username;
        this.role = u.role;
        this.isAdmin = u.role === 'ADMIN';
      }
    } catch {}
  }

  ngOnInit() {
    // decide which user to show: route param userId or current logged-in
    const routeUserId = this.route.snapshot.paramMap.get('userId');
    let currentUserId: string | null = null;
    try {
      const raw = localStorage.getItem('user');
      const u: StoredUser | null = raw ? JSON.parse(raw) : null;
      currentUserId = u?.userId || null;
    } catch {}

    const targetUserId = routeUserId || currentUserId;
    this.isOwnProfile = !routeUserId || routeUserId === currentUserId;

    if (targetUserId) {
      this.userService.getUser(targetUserId).subscribe({
        next: (u: any) => {
          this.username = u.username ?? this.username;
          this.nickname = u.nickname ?? this.nickname ?? this.username;
          this.handle = '@' + (this.username || 'user');
          this.role = u.role ?? this.role;
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
        const me = targetUserId
          ? members.find((m: any) => m.userId === targetUserId)
          : members.find((m: any) => m.username === this.username);
        const displayUserId = me?.userId || targetUserId || null;
        if (displayUserId) {
          this.matchesService.getUserMatches(displayUserId).subscribe({
            next: (dto: any) => {
              const items = dto?.matches || [];
              this.activities = items.slice(0, 10).map((m: any) => {
                const date = new Date(m.matchDate).toLocaleDateString();
                const myRole = (m.participants || []).find(
                  (p: any) => p.userId === displayUserId,
                )?.role;
                const opponentNames = (m.participants || [])
                  .filter((p: any) => p.userId !== displayUserId)
                  .sort((a: any, b: any) => (b.score ?? 0) - (a.score ?? 0))
                  .map((p: any) => p.username)
                  .join(', ');
                const scoresCombined = (() => {
                  const scores = (m.participants || [])
                    .map((p: any) => p.score ?? 0)
                    .sort((a: number, b: number) => b - a);
                  return scores.join(' - ');
                })();
                return {
                  matchId: m.matchId,
                  result: myRole === 'WINNER' ? 'WIN' : 'LOSS',
                  score: scoresCombined,
                  when: date,
                  opponents: opponentNames,
                } as any;
              });
            },
          });
        }
      },
      error: (err) => console.log(err),
    });
  }

  showPhotoModal = false;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  onEditProfilePhoto() {
    this.showPhotoModal = true;
    this.selectedFile = null;
    this.previewUrl = null;
  }
  cancelPhotoModal() {
    this.showPhotoModal = false;
    this.selectedFile = null;
    this.previewUrl = null;
  }
  onFileSelected(e: any) {
    const file: File | null = e?.target?.files?.[0] || null;
    if (!file) return;
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => (this.previewUrl = reader.result as string);
    reader.readAsDataURL(file);
  }
  uploadPhoto() {
    if (!this.previewUrl) return;
    this.userService.updateUser({ avatarUrl: this.previewUrl }).subscribe({
      next: () => {
        window.dispatchEvent(
          new CustomEvent('user-updated', {
            detail: { userId: this.userId, avatarUrl: this.previewUrl },
          }),
        );
        this.cancelPhotoModal();
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
  onChangePassword() {
    alert('Change password TBD');
  }
  showEditUsername = false;
  tempUsername = '';
  onEditUsername() {
    this.tempUsername = this.nickname || this.username || '';
    this.showEditUsername = true;
  }
  saveUsername() {
    const next = (this.tempUsername || '').trim();
    if (!next || next === this.nickname) {
      this.showEditUsername = false;
      return;
    }
    this.userService.updateUser({ nickname: next }).subscribe({
      next: () => {
        this.nickname = next;
        try {
          const raw = localStorage.getItem('user');
          if (raw) {
            const u: any = JSON.parse(raw);
            u.nickname = next;
            localStorage.setItem('user', JSON.stringify(u));
          }
        } catch {}
        window.dispatchEvent(
          new CustomEvent('user-updated', {
            detail: { userId: this.userId, username: this.username, nickname: next },
          }),
        );
        this.showEditUsername = false;
      },
      error: () => {
        alert('Failed to update username');
        this.showEditUsername = false;
      },
    });
  }
  cancelEditUsername() {
    this.showEditUsername = false;
  }
  onSignOut() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
  onBackToDashboard() {
    this.router.navigate(['/main-dashboard']);
  }

  onClickDeleteMatch(a: any) {
    if (!this.isAdmin) return;
    const verb = a.result === 'WIN' ? 'won' : 'lost';
    const text = `${this.nickname || this.username} ${verb} against ${a.opponents}`;
    this.deleteContext = { text, matchId: a.matchId } as any;
    this.showDeleteModal = true;
  }
  cancelDeleteMatch() {
    this.showDeleteModal = false;
    this.deleteContext = null;
  }
  confirmDeleteMatch() {
    const id = this.deleteContext?.matchId as string | undefined;
    if (!id) {
      this.cancelDeleteMatch();
      return;
    }
    this.matchesService.deleteMatchbyId(id).subscribe({
      next: () => {
        // remove from activities locally
        this.activities = this.activities.filter((x: any) => x.matchId !== id);
        // refresh activities from backend to stay consistent
        const displayUserId = this.userId;
        if (displayUserId) {
          this.matchesService.getUserMatches(displayUserId).subscribe({
            next: (dto: any) => {
              const items = dto?.matches || [];
              this.activities = items.slice(0, 10).map((m: any) => {
                const date = new Date(m.matchDate).toLocaleDateString();
                const myRole = (m.participants || []).find(
                  (p: any) => p.userId === displayUserId,
                )?.role;
                const opponentNames = (m.participants || [])
                  .filter((p: any) => p.userId !== displayUserId)
                  .sort((a: any, b: any) => (b.score ?? 0) - (a.score ?? 0))
                  .map((p: any) => p.username)
                  .join(', ');
                const scoresCombined = (() => {
                  const scores = (m.participants || [])
                    .map((p: any) => p.score ?? 0)
                    .sort((a: number, b: number) => b - a);
                  return scores.join(' - ');
                })();
                return {
                  matchId: m.matchId,
                  result: myRole === 'WINNER' ? 'WIN' : 'LOSS',
                  score: scoresCombined,
                  when: date,
                  opponents: opponentNames,
                } as any;
              });
            },
          });
          // refresh user stats
          this.userService.getUser(displayUserId).subscribe({
            next: (u: any) => {
              this.gamesPlayed = u.matchesPlayed || 0;
              this.totalWins = u.victories || 0;
              const wp = this.gamesPlayed
                ? Math.round((this.totalWins / this.gamesPlayed) * 100)
                : 0;
              this.winRate = wp;
            },
          });
        }
        // ask leaderboard to refresh
        window.dispatchEvent(new CustomEvent('group-refresh'));
        this.cancelDeleteMatch();
      },
      error: () => {
        alert('Failed to delete match');
        this.cancelDeleteMatch();
      },
    });
  }
}
