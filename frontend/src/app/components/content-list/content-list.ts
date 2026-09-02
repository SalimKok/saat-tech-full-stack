import { Component, inject, OnInit, ChangeDetectorRef, DestroyRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ContentService } from '../../services/contentService';
import { ContentDto } from '../../models/content';
import { ContentFilterDto } from '../../models/content-filter';
import { ContentFilter } from '../content-filter/content-filter';
import { EditModal } from '../edit-modal/edit-modal';
import { RouterModule } from '@angular/router';
import { AlertService } from '../../services/alert-service';
import { ConfirmService } from '../../services/confirm-service'; 

@Component({
  selector: 'app-content-list',
  standalone: true,
  imports: [CommonModule, ContentFilter, EditModal, RouterModule],
  templateUrl: './content-list.html',
  styleUrl: './content-list.css',
})
export class ContentList implements OnInit {
  private readonly contentService = inject(ContentService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  movies: ContentDto[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  currentPage: number = 0;
  readonly pageSize: number = 14;
  totalPages: number = 0;
  totalElements: number = 0;

    activeFilter?: ContentFilterDto = {
    title: '',
    contentType: '',
    status: 'PUBLISHED',
    genre: '',
    minRating: null,
    year: null
  } as ContentFilterDto;

  selectedDetailId: number | null = null;
  selectedMovie: ContentDto | null = null;

  isFilterOpen: boolean = false;

  constructor(
      private alertService: AlertService,
      private confirmService: ConfirmService 
    ) {}

  toggleFilter(): void {
    this.isFilterOpen = !this.isFilterOpen;
  }

  ngOnInit(): void {
    this.fetchMovies(this.currentPage);
  }

  fetchMovies(page: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.contentService.getAllContents(page, this.pageSize, this.activeFilter)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.movies = response.content ?? [];
          this.currentPage = response.number;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.alertService.showError(err.error?.message ?? 'Failed to load movies from server!');
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }

  onFilterChanged(filter: ContentFilterDto): void {
    this.activeFilter = filter;
    this.currentPage = 0;
    this.fetchMovies(0);
  }

  onNextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.fetchMovies(this.currentPage + 1);
    }
  }

  onPrevPage(): void {
    if (this.currentPage > 0) {
      this.fetchMovies(this.currentPage - 1);
    }
  }

  async onDelete(id: number | undefined): Promise<void> {
    if (!id) return;
    const confirmed = await this.confirmService.ask(
      'Delete Content', 
      'Are you sure you want to permanently delete this content? This action cannot be undone.'
    );

    if (!confirmed) return;

    this.contentService.deleteContent(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
         next: () => {
          this.alertService.showSuccess('Content successfully deleted!');

          if (this.movies.length === 1 && this.currentPage > 0) {
            this.currentPage--;
          }
          this.fetchMovies(this.currentPage);
        },
        error: (err) => {
          this.alertService.showError('Failed to delete content!');
          console.error(err);
        }
      });
  }

  openDetailModal(movie: ContentDto): void {
    if (movie.id) {
      this.selectedDetailId = movie.id;
    }
  }

  onDetailModalClosed(): void {
    this.selectedDetailId = null;
  }

  openEditModal(movie: ContentDto): void {
    this.selectedMovie = movie;
  }
  
  onModalClosed(): void {
    this.selectedMovie = null;
  }

  onMovieSaved(): void {
    this.selectedMovie = null;
    this.fetchMovies(this.currentPage);
  }
}