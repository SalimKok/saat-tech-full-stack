import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../services/token-service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  templateUrl: './auth-callback.html',
  styleUrl: './auth-callback.css'
})
export class AuthCallbackComponent implements OnInit {

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private tokenService: TokenService 
  ) {}

  ngOnInit(): void {
     this.route.fragment.subscribe(fragment => {
      
      if (fragment) {
        const params = new URLSearchParams(fragment);
        const token = params.get('token');
        if (token) {
          this.tokenService.setToken(token);
          this.router.navigate(['/movies']);
          return; 
        }
      }
      window.location.href = 'http://localhost:4202/login';
    });
  }
}
