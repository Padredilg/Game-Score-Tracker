import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root',
})
export class GroupService {
  private httpClient = inject(HttpClient);
  private url = `${environment.apiBaseUrl}/groups`;

  joinGroup(groupCode: any): Observable<any> {
    return this.httpClient.post(`${this.url}/join`, { groupCode });
  }

  toggleVisibility(payload: any): Observable<any> {
    return this.httpClient.put(`${this.url}/togglevisibility`, payload);
  }

  editGroupName(groupName: any): Observable<any> {
    return this.httpClient.put(`${this.url}/editname`, { groupName });
  }

  getGroupDetails(): Observable<any> {
    return this.httpClient.get(`${this.url}/groupdetails`);
  }

  manageGroup(payload: any): Observable<any> {
    return this.httpClient.put(`${this.url}/manage`, payload);
  }
}
