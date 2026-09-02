import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/api/auth`;

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, credentials, { withCredentials: true });
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, userData, { withCredentials: true });
  }
}
