import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentSearchService } from './services/content-search-service';
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly searchService = inject(ContentSearchService);
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
}
