import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ContentSearchService } from '../../services/content-search-service';
import { ContentIndex, SearchFilter } from '../../models/content-index';

@Component({
  selector: 'app-content-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './content-search.html',
  styleUrl: './content-search.css'
})
export class ContentSearchComponent implements OnInit{
  private readonly searchService = inject(ContentSearchService);
  private readonly cdr = inject(ChangeDetectorRef);

  showBoostSettings = false;
  selectedExplainItem: ContentIndex | null = null;

    readonly contentTypes = [
    { label: 'All', value: '' },
    { label: 'Movie', value: 'MOVIE' },
    { label: 'Series', value: 'SERIES' },
    { label: 'Season', value: 'SEASON' },
    { label: 'Episode', value: 'EPISODE' }
  ];

  readonly genres: readonly string[] = [
    'All',
    'Action',
    'Drama',
    'Crime',
    'Sci-Fi',
    'Animation',
    'Adventure'
  ];

  filter: SearchFilter = {
    query: '',
    contentType: '',
    genre: '',
    minRating: undefined,
    page: 0,
    size: 16,
    titleBoost: 3.0,
    plotBoost: 1.5,
    castBoost: 1.0,
    genreBoost: 1.0,
  };

  contents: ContentIndex[] = [];
  totalResults = 0;
  isLoading = false;

  ngOnInit(): void {
    this.fetchSearchResults();
  }

  onSearchSubmit(): void {
    this.fetchSearchResults();
  }

  onContentTypeChange(type: string): void {
    this.filter.contentType = type;
    this.fetchSearchResults();
  }

  onGenreChange(genre: string): void {
    this.filter.genre = genre === 'All' ? '' : genre;
    this.fetchSearchResults();
  }

  onSliderRatingChange(event: Event): void {
    const val = parseFloat((event.target as HTMLInputElement).value);
    this.filter.minRating = val === 0 ? undefined : val;
    this.fetchSearchResults();
  }

  onBoostChange(): void {
    this.fetchSearchResults();
  }

  resetBoosts(): void {
    this.filter.titleBoost = 3.0;
    this.filter.plotBoost = 1.5;
    this.filter.castBoost = 1.0;
    this.filter.genreBoost = 1.0;
    this.fetchSearchResults();
  }

  clearFilters(): void {
    this.filter.query = '';
    this.filter.contentType = '';
    this.filter.genre = '';
    this.filter.minRating = undefined;
    this.fetchSearchResults();
  }

  openExplainModal(item: ContentIndex): void {
    this.selectedExplainItem = item;
    this.cdr.markForCheck();
  }

  closeExplainModal(): void {
    this.selectedExplainItem = null;
    this.cdr.markForCheck();
  }


  getHighlightedTitle(item: ContentIndex): string {
    if (item.matchExplanation?.highlightedSnippets?.['title']?.length) {
      return item.matchExplanation.highlightedSnippets['title'].join(' ');
    }
    return item.title || '';
  }

  getHighlightedPlot(item: ContentIndex): string {
    if (item.matchExplanation?.highlightedSnippets?.['plot']?.length) {
      return item.matchExplanation.highlightedSnippets['plot'].join(' ... ');
    }
    return item.plot || '';
  }

  getHighlightedCast(item: ContentIndex): string {
    if (!item.castNames || item.castNames.length === 0) {
      return '';
    }

    const highlightedMap = new Map<string, string>();
    if (item.matchExplanation?.highlightedSnippets?.['castNames']) {
      for (const snippet of item.matchExplanation.highlightedSnippets['castNames']) {
        const cleanName = snippet.replace(/<[^>]*>/g, '').trim().toLowerCase();
        highlightedMap.set(cleanName, snippet);
      }
    }

    const fullCastWithHighlights = item.castNames.map(actorName => {
      const lower = actorName.toLowerCase();
      for (const [clean, highlighted] of highlightedMap.entries()) {
        if (lower.includes(clean) || clean.includes(lower)) {
          return highlighted;
        }
      }
      return actorName;
    });

    return fullCastWithHighlights.slice(0, 4).join(', ');
  }

  private fetchSearchResults(): void {
    this.isLoading = true;
    this.cdr.markForCheck();

    this.searchService.search(this.filter).subscribe({
      next: (response) => {
        this.contents = response.content || [];
        this.totalResults = response.totalElements || 0;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Search query failed:', error);
        this.isLoading = false;
        this.contents = [];
        this.totalResults = 0;
        this.cdr.markForCheck();
      }
    });
  }
}
