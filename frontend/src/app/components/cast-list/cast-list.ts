import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CastService } from '../../services/castservice';
import { CastDto } from '../../models/cast';

@Component({
  selector: 'app-cast-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cast-list.html',
  styleUrl: './cast-list.css'
})
export class CastList implements OnInit {
  private castService = inject(CastService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef); 

  casts: CastDto[] = [];
  searchTerm: string = '';
  loading: boolean = true;
  
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 24;
  totalCasts: number = 0;

  ngOnInit(): void {
    this.fetchCasts(0);
  }

  onCardClick(id?: number): void {
    if (id) {
      this.router.navigate(['/casts/edit', id]);
    }
  }


  fetchCasts(page: number): void {
    this.loading = true;
    this.currentPage = page;
    this.cdr.detectChanges(); 

    this.castService.getAllCasts(page, this.pageSize, this.searchTerm).subscribe({
      next: (data) => {
        this.casts = data.content || [];
        this.totalPages = data.totalPages || 0;
        this.totalCasts = data.totalElements || 0; 
        this.loading = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error("Backend'den veri çekerken hata oluştu:", err);
        alert("Veriler yüklenemedi. Lütfen konsolu (F12) kontrol edin.");
        this.loading = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  onSearch(): void {
    this.fetchCasts(0);
  }

  navigateToAdd(): void {
    this.router.navigate(['/casts/add']);
  }
  
  onPrevPage(): void {
    if (this.currentPage > 0) this.fetchCasts(this.currentPage - 1);
  }

  onNextPage(): void {
    if (this.currentPage < this.totalPages - 1) this.fetchCasts(this.currentPage + 1);
  }
}
