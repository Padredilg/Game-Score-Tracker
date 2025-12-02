import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { GroupService } from '../services/group.service';

@Component({
  selector: 'app-join-group',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './join-group.component.html',
  styleUrls: ['./join-group.component.scss'],
})
export class JoinGroupComponent {
  groupCode: string = '';
  errorMessage = '';

  constructor(
    private router: Router,
    private groupService: GroupService,
  ) {}

  joinGroup() {
    const code = this.groupCode.trim().toUpperCase();

    this.errorMessage = '';

    this.groupService.joinGroup(code).subscribe({
      next: (res) => {
        try {
          localStorage.setItem('user', JSON.stringify(res));
        } catch {}

        window.dispatchEvent(new CustomEvent('user-joined-group', { detail: res }));
        this.router.navigate(['/main-dashboard']);
      },
      error: (err) => {
        if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Group code not found.';
        }
      },
    });
  }
}
