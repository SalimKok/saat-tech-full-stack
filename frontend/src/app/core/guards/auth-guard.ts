import { CanActivateFn } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const platformId = inject(PLATFORM_ID);

  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('auth_token');
    
    if (token) {
      return true; 
    } else {
      window.location.href = 'http://localhost:4202/login';
      return false; 
    }
  }
  
  return false; 
};
