import {
  Component,
  inject,
  signal,
  Input,
  ViewChild,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DeletePlayerDialog } from '../delete-player-dialog/delete-player-dialog';
import { UserService } from '../services/user.service';
import { GroupService } from '../services/group.service';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'leaderboard-list',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatDialogModule,
    RouterModule,
    MatTableModule,
    MatSortModule,
  ],
  templateUrl: './leaderboard-list.html',
  styleUrl: './leaderboard-list.scss',
})
export class LeaderboardList implements OnChanges {
  @Input() searchTerm: string = '';

  dataSource = new MatTableDataSource<any>([]);
  displayedColumns = [
    'rank',
    'player',
    'winPercent',
    'totalWins',
    'gamesPlayed',
    'totalPoints',
    'highestScore',
  ];
  @ViewChild(MatSort) sort?: MatSort;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['searchTerm']) {
      this.applyFilter(this.searchTerm || '');
    }
  }

  applyFilter(term: string) {
    const value = term.trim().toLowerCase();
    this.dataSource.filterPredicate = (data: any, filter: string) => {
      return (
        (data.name || '').toLowerCase().includes(filter) ||
        (data.username || '').toLowerCase().includes(filter)
      );
    };
    this.dataSource.filter = value;
  }

  players = signal<any>([]);
  isAdmin = signal<boolean>(false);
  private groupService = inject(GroupService);

  // Column visibility state
  visibleCols = signal<Record<string, boolean>>({
    rank: true,
    player: true,
    winPercent: true,
    totalWins: true,
    gamesPlayed: true,
    totalPoints: true,
    highestScore: true,
    actions: true,
  });

  private recomputeDisplayedColumns() {
    const base = [
      'rank',
      'player',
      'winPercent',
      'totalWins',
      'gamesPlayed',
      'totalPoints',
      'highestScore',
    ];
    const cols = this.visibleCols();
    const isAdmin = this.isAdmin();
    this.displayedColumns = base.filter((c) => (isAdmin ? true : !!cols[this.mapColKey(c)]));
    if (isAdmin) this.displayedColumns = [...this.displayedColumns, 'actions'];
  }
  private mapColKey(c: string): string {
    switch (c) {
      case 'winPercent':
        return 'winPercent';
      case 'totalWins':
        return 'totalWins';
      case 'gamesPlayed':
        return 'gamesPlayed';
      case 'totalPoints':
        return 'totalPoints';
      case 'highestScore':
        return 'highestScore';
      case 'rank':
        return 'rank';
      case 'player':
        return 'player';
      default:
        return c;
    }
  }

  hideCol(key: string) {
    if (!this.isAdmin()) return;
    this.visibleCols.update((c) => ({ ...c, [key]: false }));
    this.recomputeDisplayedColumns();
  }
  showCol(key: string) {
    if (!this.isAdmin()) return;
    this.visibleCols.update((c) => ({ ...c, [key]: true }));
    this.recomputeDisplayedColumns();
  }
  toggleCol(key: string) {
    if (!this.isAdmin()) return;
    const cur = !!this.visibleCols()[key];
    if (cur) this.hideCol(key);
    else this.showCol(key);
    this.syncVisibilityToBackend();
  }

  private syncVisibilityToBackend() {
    const cols = this.visibleCols();
    const payload = {
      winPercentageVisibility: !!cols['winPercent'],
      matchesPlayedVisibility: !!cols['gamesPlayed'],
      victoriesVisibility: !!cols['totalWins'],
      defeatsVisibility: true,
      cumulativeScoreVisibility: !!cols['totalPoints'],
      highestScoreVisibility: !!cols['highestScore'],
    };
    this.groupService.toggleVisibility(payload).subscribe({
      next: () => {},
      error: (err) => console.log('Failed to toggle visibility', err),
    });
  }

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
  ) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) {
        const u = JSON.parse(raw);
        const admin = u.role === 'ADMIN';
        this.isAdmin.set(admin);
        if (admin) this.displayedColumns.push('actions');
      }
    } catch {}

    window.addEventListener('user-joined-group', (e: any) => {
      const u = e.detail;
      if (u) this.addUserToPlayers(u);
    });

    window.addEventListener('user-updated', (e: any) => {
      const d = e?.detail;
      if (!d?.userId) return;
      this.players.update((list: any[]) =>
        list.map((p: any) =>
          p.userId === d.userId
            ? {
                ...p,
                name: d.nickname || p.name,
                initials: (d.nickname || p.username || '').slice(0, 2).toUpperCase(),
                color: this.hashColor(d.nickname || p.username),
              }
            : p,
        ),
      );
      this.dataSource.data = this.players();
    });

    window.addEventListener('group-refresh', () => {
      this.groupService.getGroupDetails().subscribe({
        next: (response) => {
          const members = (response?.members || []).map((m: any) => ({
            userId: m.userId,
            username: m.username,
            name: m.nickname || m.username,
            totalWins: m.victories,
            gamesPlayed: m.matchesPlayed,
            totalPoints: m.cumulativeScore,
            highestScore: m.highestScore,
            winPercent: m.matchesPlayed ? Math.round((m.victories / m.matchesPlayed) * 100) : 0,
            initials: (m.nickname || m.username || '').slice(0, 2).toUpperCase(),
            color: this.hashColor(m.nickname || m.username),
          }));
          this.players.set(members);
          this.dataSource.data = members;
        },
      });
    });
  }

  ngOnInit() {
    this.groupService.getGroupDetails().subscribe({
      next: (response) => {
        const g = response?.group || {};
        this.visibleCols.set({
          rank: true,
          player: true,
          winPercent: !!g.winPercentageVisibility,
          totalWins: !!g.victoriesVisibility,
          gamesPlayed: !!g.matchesPlayedVisibility,
          totalPoints: !!g.cumulativeScoreVisibility,
          highestScore: !!g.highestScoreVisibility,
          actions: this.isAdmin(),
        });
        const members = (response?.members || []).map((m: any) => {
          const winPercent =
            m.winPercentage != null
              ? Math.round(m.winPercentage * 100)
              : m.matchesPlayed
                ? Math.round((m.victories / m.matchesPlayed) * 100)
                : 0;
          return {
            userId: m.userId,
            username: m.username,
            name: m.nickname || m.username,
            totalWins: m.victories,
            gamesPlayed: m.matchesPlayed,
            totalPoints: m.cumulativeScore,
            highestScore: m.highestScore,
            winPercent,
            initials: (m.nickname || m.username || '').slice(0, 2).toUpperCase(),
            color: this.hashColor(m.nickname || m.username),
          };
        });
        this.players.set(members);
        this.dataSource.data = members;
        this.recomputeDisplayedColumns();
        setTimeout(() => {
          if (this.sort) this.dataSource.sort = this.sort!;
        });
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
          username: u.username,
          name: u.nickname || u.username,
          totalWins: u.victories,
          gamesPlayed: u.matchesPlayed,
          totalPoints: u.cumulativeScore,
          highestScore: u.highestScore,
          winPercent,
          initials: (u.nickname || u.username || '').slice(0, 2).toUpperCase(),
          color: this.hashColor(u.nickname || u.username),
        };
      }),
    );
    this.dataSource.data = this.players();
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
          username: u.username,
          name: u.nickname || u.username,
          totalWins: u.victories,
          gamesPlayed: u.matchesPlayed,
          totalPoints: u.cumulativeScore,
          highestScore: u.highestScore,
          winPercent,
          initials: (u.nickname || u.username || '').slice(0, 2).toUpperCase(),
          color: this.hashColor(u.nickname || u.username),
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
          this.dataSource.data = this.players();
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
                  username: u.username,
                  name: u.nickname || u.username,
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
      this.dataSource.data = updated as any[];
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
