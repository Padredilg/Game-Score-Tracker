import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AppService } from '../app-service/app.service';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  form: FormGroup;
  submitting = signal(false);

  constructor(private fb: FormBuilder, private router: Router, private appService: AppService) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  get f() { return this.form.controls; }

  async onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);

    const payload = {
      username: this.f['username'].value,
      password: this.f['password'].value,
    };

    this.appService.login(payload).subscribe({
      next: (res: any) => {
        try {
          localStorage.setItem('token', res?.token ?? '');
          if (res?.user) localStorage.setItem('user', JSON.stringify(res.user));
        } catch {}
        const user = res?.user;
        if (user && !user.groupCode && user.role !== 'ADMIN') {
          this.router.navigate(['/join-group']);
        } else {
          this.router.navigate(['/main-dashboard']);
        }
      },
      error: (err) => {
        alert(err?.error?.message || 'Login failed');
        this.submitting.set(false);
      }
    });
  }
}
