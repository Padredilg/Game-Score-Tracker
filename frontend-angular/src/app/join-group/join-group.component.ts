import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-join-group',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './join-group.component.html',
  styleUrls: ['./join-group.component.scss']
})
export class JoinGroupComponent {
  groupCode: string = '';

  joinGroup() {
    console.log('Joining group with code:', this.groupCode);
  }
}
