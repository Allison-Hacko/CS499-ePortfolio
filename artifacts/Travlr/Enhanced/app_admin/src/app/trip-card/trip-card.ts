import { Component, Input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TripData } from '../trip';

@Component({
  selector: 'app-trip-card',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './trip-card.html',
  styleUrl: './trip-card.css'
})
export class TripCard {
  @Input({ required: true }) trip!: TripData;
  imageError = false;

  get imageUrl(): string {
    if (!this.trip.image) return '';
    const path = this.trip.image.replace(/^\/+/, '').replace(/^images\//, '');
    return `http://localhost:3000/images/${path}`;
  }
}
