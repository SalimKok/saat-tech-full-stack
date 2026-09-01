import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; // ChangeDetectorRef eklendi
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { CastService } from '../../services/castservice';
import { CastDto } from '../../models/cast';
import { CastContentDto } from '../../models/CastContentDto';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cast-detail',
  standalone: true,
  imports: [CommonModule],
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
      contentsData: this.castService.getCastContents(id)
    }).subscribe({
      next: (result) => {
        this.cast = result.castData;
        this.contents = result.contentsData;
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

  goBack(): void {
    this.router.navigate(['/casts']);
  }
}
