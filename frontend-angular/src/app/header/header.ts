import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GroupService } from '../services/group.service';

interface StoredUser {
  username: string;
  role: string;
  groupCode: string;
}

@Component({
  selector: 'header',
  imports: [MatIcon, MatTooltipModule, MatSnackBarModule, FormsModule, CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  isAdmin = false;
  username = '';
  nickname = '';
  groupCode = '';
  groupName = '';
  showEdit = false;
  tempGroupName = '';
  showManage = false;
  openForNewMembers: boolean = true;

  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private groupService: GroupService,
  ) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u: StoredUser = JSON.parse(raw);
        this.username = u.username;
        this.nickname = (u as any).nickname || u.username;
        this.groupCode = u.groupCode;
        this.isAdmin = u.role === 'ADMIN';
      }
    } catch {}
  }

  ngOnInit() {
    this.groupService.getGroupDetails().subscribe({
      next: (resp: any) => {
        this.groupName = resp?.group?.groupName || '';
        this.openForNewMembers = !!resp?.group?.openForNewMembers;
      },
      error: (err) => console.log(err),
    });
  }

  ngAfterViewInit() {
    window.addEventListener('user-updated', (e: any) => {
      const d = e?.detail;
      if (d?.nickname) this.nickname = d.nickname;
    });
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  signOut() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  openToast(message: string) {
    this.snackBar.open(message, 'Close', { duration: 2000, panelClass: ['snack-success'] });
  }

  openEditDialog() {
    this.tempGroupName = this.groupName || '';
    this.showEdit = true;
  }
  saveGroupName() {
    const name = (this.tempGroupName || '').trim();
    if (!name) {
      this.showEdit = false;
      return;
    }
    this.groupService.editGroupName(name).subscribe({
      next: (group) => {
        this.groupName = group?.groupName || name;

        this.showEdit = false;
        this.openToast('Group name updated');
      },
      error: () => {
        this.showEdit = false;
        this.openToast('Failed to update group name');
      },
    });
  }
  cancelEdit() {
    this.showEdit = false;
  }

  copyGroupCode() {
    if (!this.groupCode) {
      this.openToast('No group code to copy');
      return;
    }
    navigator.clipboard
      .writeText(this.groupCode)
      .then(() => this.openToast('Group code copied'))
      .catch(() => this.openToast('Copy failed'));
  }

  refreshGroupCode() {
    const newCode = Array.from(
      { length: 6 },
      () => 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'[Math.floor(Math.random() * 36)],
    ).join('');
    this.groupCode = newCode;
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u: any = JSON.parse(raw);
        u.groupCode = newCode;
        localStorage.setItem('user', JSON.stringify(u));
      }
    } catch {}
    this.openToast('Group code refreshed');
  }

  openManageDialog() {
    this.showManage = true;
  }
  cancelManage() {
    this.showManage = false;
  }
  confirmManage() {
    const payload = { openForNewMembers: !this.openForNewMembers } as any;
    this.groupService.manageGroup(payload).subscribe({
      next: (g: any) => {
        this.openForNewMembers = !!g?.openForNewMembers;
        this.showManage = false;
        this.openToast(this.openForNewMembers ? 'Group opened' : 'Group closed');
      },
      error: () => {
        this.showManage = false;
        this.openToast('Action failed');
      },
    });
  }
}
