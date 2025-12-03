import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class MatchesService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/matches';

  addMatch(payload: any): Observable<any> {
    return this.http.post(`${this.base}/add`, payload);
  }

  getUserMatches(userId: string): Observable<any> {
    return this.http.get(`${this.base}/user/${userId}`);
  }

  deleteMatchbyId(matchId: string): Observable<any> {
    return this.http.delete(`${this.base}/${matchId}`, { responseType: 'text' as 'json' });
  }
}
