import { Component, afterNextRender } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  templateUrl: './auth-callback.html',
  styleUrl: './auth-callback.css'
})
export class AuthCallbackComponent {
  
  constructor(private router: Router) {
    afterNextRender(() => {
      const hash = window.location.hash;
      
      if (hash && hash.includes('token=')) {
        const token = hash.replace('#token=', '');
        
        localStorage.setItem('auth_token', token);
        
        this.router.navigate(['/search']);
      } else {
        window.location.href = 'http://localhost:4202/login';
      }
    });
  }
}
