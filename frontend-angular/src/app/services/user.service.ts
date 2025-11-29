import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/users';

  getMe(): Observable<any> {
    return this.http.get(`${this.url}/me`);
  }
  getUser(id: string): Observable<any> {
    return this.http.get(`${this.url}/${id}`);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
