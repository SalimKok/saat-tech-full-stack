import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { TokenService } from './services/token-service';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { GlobalAlertComponent } from './components/global-alert/global-alert';
import { ConfirmModalComponent } from './components/confirm-modal/confirm-modal';
import { environment } from '../environments/environment';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, GlobalAlertComponent, ConfirmModalComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
  private tokenService = inject(TokenService); 
  private http = inject(HttpClient); 
  
  isSidebarExpanded = true;

  toggleSidebar(): void {
    this.isSidebarExpanded = !this.isSidebarExpanded;
  }

 logout() {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.tokenService.removeToken(); 
          window.location.href = environment.loginUrl;
        },
        error: (err) => {
          console.error('Logout failed on backend:', err);
          this.tokenService.removeToken(); 
          window.location.href = environment.loginUrl;
        }
      });
  }
}
