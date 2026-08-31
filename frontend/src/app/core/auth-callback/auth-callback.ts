import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../services/token-service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  templateUrl: './auth-callback.html',
  styleUrl: './auth-callback.css'
})
export class AuthCallbackComponent implements OnInit {

 constructor(private router: Router) {}
  ngOnInit(): void {
    // URL'de token aramaya veya TokenService ile kaydetmeye gerek kalmadı. Cookie hallediyor.
    this.router.navigate(['/movies']);
  }
}
