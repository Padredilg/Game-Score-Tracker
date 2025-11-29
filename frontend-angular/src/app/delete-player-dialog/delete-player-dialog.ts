import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'delete-player-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIcon],
  templateUrl: './delete-player-dialog.html',
  styleUrl: './delete-player-dialog.scss',
})
export class DeletePlayerDialog {
  private readonly ref = inject(MatDialogRef<DeletePlayerDialog, boolean>);
  data = inject(MAT_DIALOG_DATA) as { player: any };

  get initials(): string {
    const name = this.data?.player?.name || this.data?.player?.username || '';
    return name.slice(0, 2).toUpperCase();
  }

  get color(): string {
    const name = this.data?.player?.name || this.data?.player?.username || '';
    let hash = 0;
    for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    const hue = Math.abs(hash) % 360;
    return `hsl(${hue},70%,60%)`;
  }

  cancel() {
    this.ref.close(false);
  }
  confirm() {
    this.ref.close(true);
  }
}
