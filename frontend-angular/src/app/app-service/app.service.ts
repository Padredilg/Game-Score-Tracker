import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  private httpClient = inject(HttpClient);
  private url = `${environment.apiBaseUrl}/auth`;

  login(payload: any): Observable<any> {
    return this.httpClient.post(`${this.url}/login`, payload);
  }

  register(payload: any): Observable<any> {
    return this.httpClient.post(`${this.url}/register`, payload);
  }
}
