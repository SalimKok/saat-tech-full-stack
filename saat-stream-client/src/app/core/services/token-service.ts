import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      const match = document.cookie.match(new RegExp('(^| )auth_token=([^;]+)'));
      return match ? match[2] : null;
    }
    return null; //(SSR)
  }

  setToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) {
      document.cookie = `auth_token=${token}; path=/; max-age=86400; SameSite=Lax`;
    }
  }

  removeToken(): void {
    if (isPlatformBrowser(this.platformId)) {
      document.cookie = 'auth_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT; SameSite=Lax';
    }
  }
}
