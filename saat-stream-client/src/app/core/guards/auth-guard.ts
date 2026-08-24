import { CanActivateFn } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { TokenService } from '../services/token-service'; 

export const authGuard: CanActivateFn = (route, state) => {
  const platformId = inject(PLATFORM_ID);
  const tokenService = inject(TokenService); 

  if (isPlatformBrowser(platformId)) {
    const token = tokenService.getToken();

    if (token) {
      return true;
    } else {
      window.location.href = 'http://localhost:4202/login';
      return false;
    }
  }
  
  return true; 
};
