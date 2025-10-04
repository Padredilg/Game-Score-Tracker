import { Component, signal } from '@angular/core';
import { NewUser } from '../../models/user.model';
import { form } from '@angular/forms/signals';

@Component({
  selector: 'app-log-in',
  imports: [],
  templateUrl: './log-in.html',
  styleUrl: './log-in.scss',
})
export class LogIn {
  newUserInfo = signal<NewUser>({
    username: '',
    password: '',
  });

  newUserForm = form();
}
