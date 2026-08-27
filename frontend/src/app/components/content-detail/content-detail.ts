import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ContentService } from '../../services/contentService';
import { TrailerService } from '../../services/trailer-service';
import { TrailerDto } from '../../models/trailer';
import { ContentDto } from '../../models/content';
import { EditModal } from '../edit-modal/edit-modal';


@Component({
  selector: 'app-content-detail',
  standalone: true,
  imports: [CommonModule, EditModal],
  templateUrl: './content-detail.html',
  styleUrls: ['./content-detail.css']
})
export class ContentDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private sanitizer = inject(DomSanitizer);
  private contentService = inject(ContentService);
  private trailerService = inject(TrailerService);
  private cdr = inject(ChangeDetectorRef);

  content: ContentDto | null = null;
  trailers: TrailerDto[] = [];
  safeTrailerUrl: SafeResourceUrl | null = null;
  
  activeTrailerUrl = '';
  activeThumbnailUrl = '';
  isVideoPlaying = false;
  
  isLoading = true;
  error = '';

  selectedMovie: ContentDto | null = null;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const contentId = Number(idParam);
      this.loadContentAndTrailers(contentId);
    } else {
      this.error = 'Invalid content ID in URL.';
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }

  private loadContentAndTrailers(contentId: number): void {
    this.contentService.getContentById(contentId).subscribe({
      next: (data) => {
        this.content = data;
        this.fetchTrailers(contentId);
      },
      error: (err) => {
        this.error = 'Failed to load content details.';
        this.isLoading = false;
        console.error('Content Load Error:', err);
        this.cdr.markForCheck();
      }
    });
  }

  private fetchTrailers(contentId: number): void {
    this.trailerService.fetchTrailers(contentId).subscribe({
      next: (data) => {
        const uniqueTrailers: TrailerDto[] = [];
        const seenTypes = new Set<string>();

        const sortedTrailers = data.sort((a, b) => {
          if (a.type === 'Trailer' && b.type !== 'Trailer') return -1;
          if (b.type === 'Trailer' && a.type !== 'Trailer') return 1;
          return 0;
        });

        for (const trailer of sortedTrailers) {
          if (!seenTypes.has(trailer.type)) {
            seenTypes.add(trailer.type);
            uniqueTrailers.push(trailer);
          }
        }
        
        this.trailers = uniqueTrailers;

        if (this.trailers && this.trailers.length > 0) {
          this.activeTrailerUrl = this.trailers[0].youtubeEmbedUrl;
          this.activeThumbnailUrl = this.trailers[0].youtubeThumbnailUrl;
        }
        
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = 'Failed to load trailers.';
        this.isLoading = false;
        console.error('Trailer Load Error:', err);
        this.cdr.markForCheck();
      }
    });
  }

  playVideo(): void {
    if (this.activeTrailerUrl) {
      const autoPlayUrl = this.activeTrailerUrl.includes('?') 
        ? `${this.activeTrailerUrl}&autoplay=1` 
        : `${this.activeTrailerUrl}?autoplay=1`;
        
      this.safeTrailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(autoPlayUrl);
      this.isVideoPlaying = true;
      this.cdr.markForCheck();
    }
  }

  setTrailer(trailer: TrailerDto): void {
    this.activeTrailerUrl = trailer.youtubeEmbedUrl;
    this.activeThumbnailUrl = trailer.youtubeThumbnailUrl;
    this.isVideoPlaying = false; 
    this.safeTrailerUrl = null;
    this.cdr.markForCheck();
  }

  goBack(): void {
    this.router.navigate(['/movies']);
  }

  editContent(): void {
    if (this.content) {
      this.selectedMovie = this.content;
    }
  }
  onModalClosed(): void {
    this.selectedMovie = null;
  }
  onMovieSaved(): void {
    this.selectedMovie = null;
    if (this.content?.id) {
      this.isLoading = true; 
      this.loadContentAndTrailers(this.content.id);
    }
  }

  deleteContent(): void {
    const contentId = this.content?.id;
    
    if (contentId && confirm('Are you sure you want to delete this content?')) {
      this.contentService.deleteContent(contentId).subscribe({
         next: () => {
           this.router.navigate(['/movies']);
         },
         error: (err) => {
           console.error('Delete error', err);
           alert('Failed to delete content.');
         }
      });
    }
  }

  get actors() {
    return this.content?.casts?.filter(c => c.role === 'ACTOR').slice(0, 10) || [];
  }
  
  get directors() {
    return this.content?.casts?.filter(c => c.role === 'DIRECTOR').slice(0, 3) || [];
  }
  
  get writers() {
    return this.content?.casts?.filter(c => c.role === 'WRITER').slice(0, 3) || [];
  }

  getNames(castList: any[]): string {
    return castList.map(c => c.castName || c.cast?.name || 'Unknown').join(', ');
  }
}
