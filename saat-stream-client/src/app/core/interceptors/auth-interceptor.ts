import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError } from 'rxjs';
import { TokenService } from '../services/token-service'; 

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  const tokenService = inject(TokenService); 
  
  if (isPlatformBrowser(platformId)) {
    req = req.clone({
      withCredentials: true
    });
  }
  
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && isPlatformBrowser(platformId)) {
        tokenService.removeToken();
        window.location.href = 'http://localhost:4202/login';
      }
      return throwError(() => error);
    })
  );
};
