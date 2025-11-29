import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

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

  constructor(private fb: FormBuilder) {
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

    // TODO: REPLACE THIS WITH REAL BACKEND CALL
    await new Promise((r) => setTimeout(r, 800));
    console.log('Register payload:', this.form.value);

    alert('Account created! (Hook this up to the backend API later)');
    this.submitting.set(false);
  }
}
