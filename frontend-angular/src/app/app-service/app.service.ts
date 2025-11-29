import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  private httpClient = inject(HttpClient);
  private url = 'http://localhost:8080/api/auth';

  login(payload: any): Observable<any> {
    return this.httpClient.post(`${this.url}/login`, payload);
  }

  register(payload: any): Observable<any> {
    return this.httpClient.post(`${this.url}/register`, payload);
  }

  
}
