import { Routes } from '@angular/router';
import { TripListing } from './trip-listing/trip-listing';
import { Login } from './login/login';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: '', component: TripListing, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
