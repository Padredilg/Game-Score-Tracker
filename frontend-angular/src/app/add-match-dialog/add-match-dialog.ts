import { Component, inject } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { GroupService } from '../services/group.service';
import { AddMatch } from '../models/add-match.model';

@Component({
  selector: 'add-match-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIcon,
  ],
  templateUrl: './add-match-dialog.html',
  styleUrl: './add-match-dialog.scss',
})
export class AddMatchDialog {
  private readonly ref = inject(MatDialogRef<AddMatchDialog, AddMatch | null>);
  model: AddMatch = {};
  playedAtStr = '';
  teammatesStr = '';
  opponentsStr = '';

  groupMembers: { userId: string; username: string }[] = [];

  teammatesUI: { userId?: string; name: string; score?: number }[] = [
    { name: '', score: undefined },
  ];
  opponentsUI: { userId?: string; name: string; score?: number }[] = [
    { name: '', score: undefined },
  ];

  constructor(private groupService: GroupService) {}

  ngOnInit() {
    let currentUserId: string | null = null;
    try {
      const raw = localStorage.getItem('user');
      const u = raw ? JSON.parse(raw) : null;
      currentUserId = u?.userId || null;
    } catch {}
    this.groupService.getGroupDetails().subscribe({
      next: (resp: any) => {
        const members = resp?.members || [];
        this.groupMembers = currentUserId
          ? members.filter((m: any) => m.userId !== currentUserId)
          : members;
      },
      error: (err) => console.log(err),
    });
  }

  addTeammate() {
    this.teammatesUI.push({ name: '', score: undefined });
  }
  addOpponent() {
    this.opponentsUI.push({ name: '', score: undefined });
  }
  removeTeammate(i: number) {
    this.teammatesUI.splice(i, 1);
  }
  removeOpponent(i: number) {
    this.opponentsUI.splice(i, 1);
  }

  submit() {
    const fmtDate = (() => {
      const s = this.playedAtStr || '';
      const m = s.match(/^(\d{4})-(\d{2})-(\d{2})$/);
      return m ? `${m[2]}/${m[3]}/${m[1]}` : s;
    })();
    const payload: any = {
      date: fmtDate,
      result: (this.model.result || 'draw').toUpperCase(),
      winners: {},
      losers: {},
    };
    // current user from localStorage
    try {
      const raw = localStorage.getItem('user');
      const u = raw ? JSON.parse(raw) : null;
      if (u?.userId && this.model.myScore != null) {
        const meId = String(u.userId);
        const myScore = Number(this.model.myScore);
        const res = String(this.model.result || 'draw').toLowerCase();
        if (res === 'win') {
          payload.winners[meId] = myScore;
        } else if (res === 'loss') {
          payload.losers[meId] = myScore;
        } else {
          payload.winners[meId] = myScore;
        }
      }
    } catch {}

    for (const o of this.opponentsUI) {
      const id = (o as any).userId || (o.name || '').trim();
      if (!id) continue;
      payload.losers[String(id)] = Number(o.score || 0);
    }

    for (const t of this.teammatesUI) {
      const id = (t as any).userId || (t.name || '').trim();
      if (!id) continue;
      payload.winners[String(id)] = Number(t.score || 0);
    }
    console.log(payload);
    this.ref.close(payload);
  }

  close() {
    this.ref.close(null);
  }
}
