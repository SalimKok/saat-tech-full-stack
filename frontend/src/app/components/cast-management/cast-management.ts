import { Component, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import { CastService } from '../../services/castservice';
import { CastDto } from '../../models/cast';

@Component({
  selector: 'app-cast-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cast-management.html',
  styleUrl: './cast-management.css'
})
export class CastManagement implements OnInit, OnDestroy {
  private castService = inject(CastService);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();
  private searchSubject = new Subject<string>();

  casts: CastDto[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Sayfalama Değişkenleri
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  pageSize: number = 10;

  // Yeni kişi formu
  newCastName: string = '';
  newCastPoster: string = '';
  isAdding: boolean = false;

  // Satır içi düzenleme
  editingCastId: number | null = null;
  editingCastName: string = '';
  editingCastPoster: string = '';

  ngOnInit(): void {
    this.fetchCasts(0);

    // Canlı arama için debounce
    this.searchSubject.pipe(
      debounceTime(350),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.fetchCasts(0);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchCasts(page: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.currentPage = page;
    this.cdr.detectChanges();

    this.castService.getAllCasts(page, this.pageSize, this.searchTerm)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.casts = data.content || [];
          this.totalPages = data.totalPages || 0;
          this.totalElements = data.totalElements || 0;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to load cast members!';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm);
  }

  onPrevPage(): void {
    if (this.currentPage > 0) {
      this.fetchCasts(this.currentPage - 1);
    }
  }

  onNextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.fetchCasts(this.currentPage + 1);
    }
  }

  onAddCast(): void {
    if (!this.newCastName.trim()) return;
    this.isAdding = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.castService.saveCast({ 
      name: this.newCastName.trim(),
      poster: this.newCastPoster.trim() 
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (savedCast) => {
          this.newCastName = '';
          this.newCastPoster = '';
          this.isAdding = false;
          this.successMessage = `"${savedCast.name}" has been successfully added!`;
          this.fetchCasts(0);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to add person!';
          this.isAdding = false;
          this.cdr.detectChanges();
        }
      });
  }

  startEdit(cast: CastDto): void {
    if (!cast.id) return;
    this.editingCastId = cast.id;
    this.editingCastName = cast.name;
    this.editingCastPoster = cast.poster || '';
  }

  cancelEdit(): void {
    this.editingCastId = null;
    this.editingCastName = '';
    this.editingCastPoster = '';
  }

  saveEdit(id: number): void {
    if (!this.editingCastName.trim()) return;
    this.castService.updateCast(id, { 
      name: this.editingCastName.trim(),
      poster: this.editingCastPoster.trim()
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updated) => {
          const index = this.casts.findIndex(c => c.id === id);
          if (index !== -1) {
            this.casts[index] = updated;
          }
          this.cancelEdit();
          this.successMessage = `Cast updated to "${updated.name}"`;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to update cast!';
          this.cdr.detectChanges();
        }
      });
  }

  onDelete(id: number | undefined, name: string): void {
    if (!id) return;
    if (!confirm(`Are you sure you want to delete "${name}"?`)) return;

    this.castService.deleteCast(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `"${name}" was deleted successfully.`;
          this.fetchCasts(this.currentPage);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to delete cast!';
          this.cdr.detectChanges();
        }
      });
  }
}
