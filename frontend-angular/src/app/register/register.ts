import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AppService } from '../app-service/app.service';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {
  form: FormGroup;
  submitting = signal(false);
  passwordMismatch = signal(false);

  constructor(private fb: FormBuilder, private appService: AppService, private router: Router) {
    this.form = this.fb.group({
      isAdmin: [false],
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(4)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  get f() {
    return this.form.controls;
  }

  async onSubmit() {
    this.passwordMismatch.set(false);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const password = this.f['password'].value;
    const confirm = this.f['confirmPassword'].value;

    if (password !== confirm) {
      this.passwordMismatch.set(true);
      return;
    }

    this.submitting.set(true);

    const payload = {
      username: this.f['username'].value,
      password: password,
      role: this.f['isAdmin'].value ? 'ADMIN' : 'PLAYER'
    };

    this.appService.register(payload).subscribe({
      next: () => {
        // Auto login after successful registration
        this.appService.login({ username: payload.username, password: payload.password }).subscribe({
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
          error: () => {
            alert('Registered but auto-login failed. Please sign in.');
            this.router.navigate(['/login']);
          }
        });
        this.submitting.set(false);
      },
      error: (err) => {
        alert(err?.error?.message || 'Registration failed');
        this.submitting.set(false);
      }
    });
  }
}
