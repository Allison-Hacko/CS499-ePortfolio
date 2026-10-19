import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TripData {
  _id?: string;
  code: string;
  name: string;
  length: string;
  start: string;
  resort: string;
  perPerson: string;
  image: string;
  description: string;
}

@Injectable({ providedIn: 'root' })
export class Trip {
  private apiUrl = '/api/trips';

  constructor(private http: HttpClient) {}

  getTrips(): Observable<TripData[]> {
    return this.http.get<TripData[]>(this.apiUrl);
  }

  getTrip(code: string): Observable<TripData> {
    return this.http.get<TripData>(`${this.apiUrl}/${encodeURIComponent(code)}`);
  }

  addTrip(trip: TripData): Observable<TripData> {
    return this.http.post<TripData>(this.apiUrl, trip);
  }

  updateTrip(code: string, trip: TripData): Observable<TripData> {
    return this.http.put<TripData>(`${this.apiUrl}/${encodeURIComponent(code)}`, trip);
  }

  deleteTrip(code: string): Observable<unknown> {
    return this.http.delete(`${this.apiUrl}/${encodeURIComponent(code)}`);
  }
}
