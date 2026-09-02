import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; // ChangeDetectorRef eklendi
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { CastService } from '../../services/castservice';
import { CastDto } from '../../models/cast';
import { CastContentDto } from '../../models/CastContentDto';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cast-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cast-detail.html',
  styleUrl: './cast-detail.css'
})
export class CastDetail implements OnInit {
  private castService = inject(CastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef); 

  cast: CastDto | null = null;
  contents: CastContentDto[] = [];
  loading: boolean = true;
  errorMessage: string = '';
  
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;


  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadCastData(Number(idParam));
    } else {
      this.goBack();
    }
  }

  loadCastData(id: number): void {
    this.loading = true;
    
    forkJoin({
      castData: this.castService.getCastById(id),
      contentsData: this.castService.getCastContents(id, this.currentPage, this.pageSize)
    }).subscribe({
      next: (result) => {
        this.cast = result.castData;
        this.contents = result.contentsData.content;
        this.totalPages = result.contentsData.totalPages;
        this.loading = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Failed to load cast details', err);
        this.errorMessage = 'Could not load profile.';
        this.loading = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  loadContentsPage(page: number): void {
    if (!this.cast || !this.cast.id || page < 0 || page >= this.totalPages) return;
    
    this.currentPage = page;
    this.castService.getCastContents(this.cast.id, this.currentPage, this.pageSize)
      .subscribe({
        next: (res) => {
          this.contents = res.content;
          this.totalPages = res.totalPages;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Failed to load contents page', err)
      });
  }

  goBack(): void {
    this.router.navigate(['/casts']);
  }
}
