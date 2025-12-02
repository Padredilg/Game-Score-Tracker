import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/users';

  private authHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders(token ? { Authorization: `Bearer ${token}` } : {});
    return { headers };
  }

  getMe(): Observable<any> {
    return this.http.get(`${this.url}/me`, this.authHeaders());
  }

  updateUser(userInfo: any): Observable<any> {
    return this.http.put(`${this.url}/me/update`, userInfo, this.authHeaders());
  }
  getUser(id: string): Observable<any> {
    return this.http.get(`${this.url}/${id}`, this.authHeaders());
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`, this.authHeaders());
  }
}
