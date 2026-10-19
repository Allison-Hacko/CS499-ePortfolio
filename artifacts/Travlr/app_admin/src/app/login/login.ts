import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(
    private auth: AuthenticationService,
    private router: Router
  ) {}

  submit(): void {
    this.errorMessage = '';
    this.loading = true;

    this.auth.login(this.email.trim(), this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: error => {
        this.loading = false;
        this.errorMessage =
          error.error?.message || 'Login failed. Check your email and password.';
      }
    });
  }
}
