import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

@Injectable({ providedIn: 'root' })
export class MatchesService {
  private http = inject(HttpClient);
  private url = `${environment.apiBaseUrl}/matches`;

  addMatch(payload: any): Observable<any> {
    return this.http.post(`${this.url}/add`, payload);
  }

  getUserMatches(userId: string): Observable<any> {
    return this.http.get(`${this.url}/user/${userId}`);
  }

  deleteMatchbyId(matchId: string): Observable<any> {
    return this.http.delete(`${this.url}/${matchId}`, { responseType: 'text' as 'json' });
  }
}
