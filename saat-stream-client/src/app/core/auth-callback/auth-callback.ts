import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../services/token-service'; 

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: '<h2>Authenticating... Please wait.</h2>'
})

export class AuthCallbackComponent implements OnInit {

  constructor(private router: Router) {}
  ngOnInit(): void {
    // Cookie zaten tarayıcıda var, direkt içeri al.
    this.router.navigate(['/']); 
  }
}
