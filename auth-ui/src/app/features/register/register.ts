import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth-service';
import { environment } from '../../../environments/environment.development';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: '../login/login.css' 
})
export class RegisterComponent {
  userData = { username: '', password: ''};
  
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(this.userData).subscribe({
      next: (response) => {
        this.isLoading = false;
        
        if (response && response.role) {
          this.successMessage = 'Registration successful! Redirecting to application...';
          setTimeout(() => {
            window.location.href = `${environment.apiUrl}/auth-callback`;
          }, 1500);
        } else {
          this.successMessage = 'Registration successful! Redirecting to login...';
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 1500);
        }
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 400 || err.status === 409) {
          this.errorMessage = 'Username already exists or invalid data!';
        } else {
          this.errorMessage = 'Unable to connect to the server. Please try again later.';
        }
      }
    });
  }
}