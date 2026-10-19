import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Trip, TripData } from '../trip';
import { TripCard } from '../trip-card/trip-card';

@Component({
  selector: 'app-trip-listing',
  standalone: true,
  imports: [FormsModule, TripCard],
  templateUrl: './trip-listing.html',
  styleUrl: './trip-listing.css'
})
export class TripListing implements OnInit {
  trips: TripData[] = [];
  errorMessage = '';
  successMessage = '';
  showForm = false;
  saving = false;
  editingCode: string | null = null;

  newTrip: TripData = this.emptyTrip();

  constructor(private tripService: Trip) {}

  ngOnInit(): void {
    this.loadTrips();
  }

  emptyTrip(): TripData {
    return {
      code: '',
      name: '',
      length: '',
      start: '',
      resort: '',
      perPerson: '',
      image: '',
      description: ''
    };
  }

  loadTrips(): void {
    this.tripService.getTrips().subscribe({
      next: trips => {
        this.trips = trips;
        this.errorMessage = '';
      },
      error: () => {
        this.errorMessage = 'Could not load trips. Check that Express and MongoDB are running.';
      }
    });
  }

  openAddForm(): void {
    this.editingCode = null;
    this.newTrip = this.emptyTrip();
    this.errorMessage = '';
    this.successMessage = '';
    this.showForm = true;
  }

  editTrip(trip: TripData): void {
    this.editingCode = trip.code;
    this.newTrip = {
      ...trip,
      start: trip.start ? trip.start.substring(0, 10) : ''
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.showForm = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingCode = null;
    this.newTrip = this.emptyTrip();
    this.errorMessage = '';
  }

  saveTrip(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request = this.editingCode
      ? this.tripService.updateTrip(this.editingCode, this.newTrip)
      : this.tripService.addTrip(this.newTrip);

    request.subscribe({
      next: () => {
        this.successMessage = this.editingCode
          ? 'Trip updated successfully!'
          : 'Trip added successfully!';
        this.showForm = false;
        this.editingCode = null;
        this.newTrip = this.emptyTrip();
        this.saving = false;
        this.loadTrips();
      },
      error: error => {
        this.errorMessage =
          error.error?.message ||
          'Could not save the trip. Check the information and try again.';
        this.saving = false;
      }
    });
  }

  deleteTrip(trip: TripData): void {
    if (!window.confirm(`Delete "${trip.name}"? This cannot be undone.`)) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.tripService.deleteTrip(trip.code).subscribe({
      next: () => {
        this.successMessage = `${trip.name} was deleted successfully.`;
        this.loadTrips();
      },
      error: error => {
        this.errorMessage =
          error.error?.message || 'Could not delete this trip.';
      }
    });
  }
}
