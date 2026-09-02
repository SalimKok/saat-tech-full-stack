import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentSearchService } from './services/content-search-service';
import { RouterOutlet } from "@angular/router";
import { TokenService } from './core/services/token-service'; 
import { HttpClient } from '@angular/common/http'; 
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly searchService = inject(ContentSearchService);
  private tokenService = inject(TokenService);
  private http = inject(HttpClient); 
  
  isSyncing = false;

  syncIndex(): void {
    if (this.isSyncing) {
      return;
    }

    this.isSyncing = true;
    this.searchService.syncElasticsearch().subscribe({
      next: () => {
        this.isSyncing = false;
        alert('All contents synchronized to Elasticsearch successfully!');
        window.location.reload();
      },
      error: (error) => {
        console.error('Elasticsearch synchronization failed:', error);
        this.isSyncing = false;
        alert('Failed to synchronize contents. Please check server logs.');
      }
    });
  }

  logout() {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.tokenService.removeToken(); 
          window.location.href = environment.loginUrl;;
        },
        error: (err) => {
          console.error('Logout failed on backend:', err);
          this.tokenService.removeToken(); 
          window.location.href = environment.loginUrl;;
        }
      });
  }
}
