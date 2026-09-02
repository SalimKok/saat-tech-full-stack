import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth-service';
import { environment } from '../../../environments/environment.development';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: (response) => {

        if (response && response.role) {
          const role = response.role;

          if (role === 'ADMIN') {
            window.location.href = `${environment.adminUrl}/auth-callback`;
          } else {
            window.location.href = `${environment.streamClientUrl}/auth-callback`;
          }
        }
      },
            error: (err) => {
        this.isLoading = false;
        if (err.status === 403 || err.status === 401) {
          this.errorMessage = 'Invalid username or password!';
        } else {
          this.errorMessage = 'Unable to connect to the server. Please try again later.';
        }
      }
    });
  }
}
