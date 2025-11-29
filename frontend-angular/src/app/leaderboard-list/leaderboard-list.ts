import { Component, inject, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DeletePlayerDialog } from '../delete-player-dialog/delete-player-dialog';
import { UserService } from '../services/user.service';
import { GroupService } from '../services/group.service';

@Component({
  selector: 'leaderboard-list',
  imports: [MatIcon, MatDialogModule],
  templateUrl: './leaderboard-list.html',
  styleUrl: './leaderboard-list.scss',
})
export class LeaderboardList {
  players = signal<any>([]);
  isAdmin = signal<boolean>(false);
  private groupService = inject(GroupService);

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
  ) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u = JSON.parse(raw);
        this.isAdmin.set(u.role === 'ADMIN');
        this.addUserToPlayers(u); 
      }
    } catch {}

    
    window.addEventListener('user-joined-group', (e: any) => {
      const u = e.detail;
      if (u) this.addUserToPlayers(u);
    });
  }

  ngOnInit() {
    this.groupService.getGroupDetails().subscribe({
      next: (response) => {
        const members = (response?.members || []).map((m: any) => {
          const winPercent =
            m.winPercentage != null
              ? Math.round(m.winPercentage * 100)
              : m.matchesPlayed
                ? Math.round((m.victories / m.matchesPlayed) * 100)
                : 0;
          return {
            userId: m.userId,
            name: m.username,
            totalWins: m.victories,
            gamesPlayed: m.matchesPlayed,
            totalPoints: m.cumulativeScore,
            highestScore: m.highestScore,
            winPercent,
            initials: (m.username || '').slice(0, 2).toUpperCase(),
            color: this.hashColor(m.username),
          };
        });
        this.players.set(members);
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  applyMatchReport(report: any) {
    const updated = report?.updatedUsers || [];
    if (!Array.isArray(updated) || !updated.length) return;
    const map = new Map(updated.map((u: any) => [u.userId, u] as const));
    this.players.update((list: any[]) =>
      list.map((p: any) => {
        const u = map.get(p.userId);
        if (!u) return p;
        const winPercent = u.matchesPlayed ? Math.round((u.victories / u.matchesPlayed) * 100) : 0;
        return {
          ...p,
          name: u.username,
          totalWins: u.victories,
          gamesPlayed: u.matchesPlayed,
          totalPoints: u.cumulativeScore,
          highestScore: u.highestScore,
          winPercent,
          initials: (u.username || '').slice(0, 2).toUpperCase(),
          color: this.hashColor(u.username),
        };
      }),
    );
  }

  private addUserToPlayers(u: any) {
    if (!u || !u.userId) return;
    this.players.update((list) => {
      if (list.some((p: any) => p.userId === u.userId)) return list;
      const winPercent =
        u.winPercentage != null
          ? u.winPercentage
          : u.matchesPlayed
            ? Math.round((u.victories / u.matchesPlayed) * 100)
            : 0;
      return [
        ...list,
        {
          userId: u.userId,
          name: u.username,
          totalWins: u.victories,
          gamesPlayed: u.matchesPlayed,
          totalPoints: u.cumulativeScore,
          highestScore: u.highestScore,
          winPercent,
          initials: (u.username || '').slice(0, 2).toUpperCase(),
          color: this.hashColor(u.username),
        },
      ];
    });
  }

  deletePlayer(p: any) {
    const ref = this.dialog.open(DeletePlayerDialog, { data: { player: p } });
    ref.afterClosed().subscribe((ok) => {
      if (!ok) return;
      const id = p.userId;

      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.players.update((list) => list.filter((x: any) => x !== p));
        },
        error: (err) => alert(err?.error?.message || 'Failed to remove user'),
      });
    });
  }

 
  recomputeFromLocalStorage() {
    let userName: string | null = null;
    try {
      const u = localStorage.getItem('user');
      userName = u ? JSON.parse(u).username : null;
    } catch {}
    let matches: any[] = [];
    try {
      const m = localStorage.getItem('matches');
      matches = m ? JSON.parse(m) : [];
    } catch {}
    if (!matches.length) {
      try {
        const raw = localStorage.getItem('user');
        if (raw) {
          const u = JSON.parse(raw);
          this.players.update((list) => {
            if (!list.some((p: any) => p.userId === u.userId)) {
              const winPercent = u.matchesPlayed
                ? Math.round((u.victories / u.matchesPlayed) * 10000) / 100
                : 0;
              return [
                ...list,
                {
                  userId: u.userId,
                  name: u.username,
                  totalWins: u.victories,
                  gamesPlayed: u.matchesPlayed,
                  totalPoints: u.cumulativeScore,
                  highestScore: u.highestScore,
                  winPercent,
                },
              ];
            }
            return list;
          });
        }
      } catch {}
      return;
    }

    const stats = new Map<string, { wins: number; games: number; points: number; hi: number }>();
    const add = (name: string, won: boolean, score?: number) => {
      if (!name) return;
      const s = stats.get(name) || { wins: 0, games: 0, points: 0, hi: 0 };
      s.games += 1;
      if (won) s.wins += 1;
      const sc = Number(score || 0);
      s.points += sc;
      s.hi = Math.max(s.hi, sc);
      stats.set(name, s);
    };

    for (const match of matches) {
      const won = match.result === 'win';
      if (userName) add(userName, won, match.myScore);
      for (const t of match.teammatesDetailed || []) add(t.name, won, t.score);
      for (const o of match.opponentsDetailed || []) add(o.name, !won, o.score);
    }

    this.players.update((arr: any[]) => {
      const byName = new Map(arr.map((p) => [p.name, p] as const));
      const updated = arr.map((p) => {
        const s = stats.get(p.name);
        if (!s) return p;
        const winPercent = s.games ? Math.round((s.wins / s.games) * 100) : 0;
        return {
          ...p,
          totalWins: s.wins,
          gamesPlayed: s.games,
          totalPoints: s.points,
          highestScore: s.hi,
          winPercent,
        };
      });
      for (const [name, s] of stats) {
        if (!byName.has(name)) {
          const winPercent = s.games ? Math.round((s.wins / s.games) * 100) : 0;
          updated.push({
            name,
            totalWins: s.wins,
            gamesPlayed: s.games,
            totalPoints: s.points,
            highestScore: s.hi,
            winPercent,
          });
        }
      }
      return updated;
    });
  }
  private hashColor(name: string): string {
    if (!name) return '#666';
    let hash = 0;
    for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    const hue = Math.abs(hash) % 360;
    return `hsl(${hue},70%,60%)`;
  }
}
