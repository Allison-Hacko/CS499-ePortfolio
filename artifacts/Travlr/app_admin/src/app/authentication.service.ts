import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  user: { name: string; email: string };
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly tokenKey = 'travlr_token';
  private readonly apiUrl = '/api';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, {
      email,
      password
    }).pipe(tap(response => {
      localStorage.setItem(this.tokenKey, response.token);
      localStorage.setItem('travlr_user', JSON.stringify(response.user));
    }));
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('travlr_user');
  }
}
