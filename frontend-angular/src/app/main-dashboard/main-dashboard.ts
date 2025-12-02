import { Component, ViewChild } from '@angular/core';
import { LeaderboardList } from '../leaderboard-list/leaderboard-list';
import { MatIconModule } from '@angular/material/icon';
import { Header } from '../header/header';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AddMatchDialog } from '../add-match-dialog/add-match-dialog';
import { MatchesService } from '../services/matches.service';

interface UserStats {
  username: string;
  role: string;
  groupCode: string;
  cumulativeScore: number;
  highestScore: number;
  matchesPlayed: number;
  victories: number;
  defeats: number;
  userId: string;
}

@Component({
  selector: 'main-dashboard',
  standalone: true,
  imports: [CommonModule, LeaderboardList, Header, MatIconModule, MatDialogModule],
  templateUrl: './main-dashboard.html',
  styleUrl: './main-dashboard.scss',
})
export class MainDashboard {
  user: UserStats | null = null;
  matches: any[] = [];
  @ViewChild(LeaderboardList) leaderboard?: LeaderboardList;
  searchTerm = '';

  constructor(
    private dialog: MatDialog,
    private matchesService: MatchesService,
  ) {
    try {
      const raw = localStorage.getItem('user');
      if (raw) this.user = JSON.parse(raw);
    } catch {}
  }

  onSearch(e: any) {
    this.searchTerm = (e?.target?.value || '').toLowerCase();
  }

  openAddMatch() {
    const ref = this.dialog.open(AddMatchDialog);
    ref.afterClosed().subscribe((result) => {
      if (result) {
        this.matchesService.addMatch(result).subscribe({
          next: (report) => {
            this.leaderboard?.applyMatchReport(report);
            if (this.user?.userId) {
              this.matchesService.getUserMatches(this.user.userId).subscribe();
            }
          },
        });
      }
    });
  }
}
