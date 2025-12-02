import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GroupService } from '../services/group.service';
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

  constructor(private router: Router) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u: StoredUser = JSON.parse(raw);
        this.username = u.username;
        this.nickname = (u as any).nickname || u.username;
        this.userId = (u as any).userId || null;
        this.handle = '@' + this.username;
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
}
