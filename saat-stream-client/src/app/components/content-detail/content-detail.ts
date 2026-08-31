import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ContentDetailService } from '../../services/content-detail-service';
import { VideoPlayerComponent } from '../video-player/video-player';

@Component({
  selector: 'app-content-detail',
  standalone: true,
  imports: [CommonModule, VideoPlayerComponent],
  templateUrl: './content-detail.html',
  styleUrls: ['./content-detail.css']
})
export class ContentDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private contentService = inject(ContentDetailService);
  private location = inject(Location);
  private sanitizer = inject(DomSanitizer);
  private cdr = inject(ChangeDetectorRef);

  content: any;
  trailers: any[] = [];
  actors: any[] = [];
  directors: any[] = [];
  writers: any[] = [];

  isLoading: boolean = true;
  error: string = '';

  activeTrailer: any = null;
  activeThumbnailUrl: string | null = null;
  safeTrailerUrl: SafeResourceUrl | null = null;
  isVideoPlaying: boolean = false;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadContent(+idParam);
    } else {
      this.error = 'Invalid content ID';
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }

  loadContent(id: number): void {
    this.contentService.getContentById(id).subscribe({
      next: (data) => {
        this.content = data;
        
        this.trailers = data.trailers || [];
        this.actors = data.casts?.filter((c: any) => c.role?.toUpperCase() === 'ACTOR') || [];
        this.directors = data.casts?.filter((c: any) => c.role?.toUpperCase() === 'DIRECTOR') || [];
        this.writers = data.casts?.filter((c: any) => c.role?.toUpperCase() === 'WRITER') || [];


        if (this.trailers.length > 0) {
          this.setTrailer(this.trailers[0]);
        }

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error("HATA:", err);
        this.error = 'Failed to load masterpiece.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  setTrailer(trailer: any): void {
    this.activeTrailer = trailer;
    this.isVideoPlaying = false; 

    if (trailer.site === 'YouTube') {
      const videoId = trailer.youtubeKey || this.extractYoutubeId(trailer.url || trailer.youtubeEmbedUrl);
      if (videoId) {
        this.activeThumbnailUrl = `https://img.youtube.com/vi/${videoId}/maxresdefault.jpg`;
        const embedUrl = `https://www.youtube.com/embed/${videoId}?autoplay=1`;
        this.safeTrailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
      }
    } else if (trailer.site === 'Local') {
      this.activeThumbnailUrl = this.content?.metadata?.poster || null; 
      this.safeTrailerUrl = null; 
    }
    
    this.cdr.markForCheck(); 
  }

  playVideo(): void {
    this.isVideoPlaying = true;
    this.cdr.markForCheck(); 
  }

  goBack(): void {
    this.location.back();
  }

  getNames(castArray: any[]): string {
    if (!castArray || !Array.isArray(castArray)) return '';
    return castArray.map(c => c?.cast?.name).filter(name => !!name).join(', ');
  }

  getGenres(): string[] {
    if (!this.content?.metadata?.genre) return [];
    return this.content.metadata.genre.split(',').map((g: string) => g.trim()).filter((g: string) => !!g);
  }

  private extractYoutubeId(url: string | undefined): string | null {
    if (!url) return null;
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return (match && match[2].length === 11) ? match[2] : null;
  }
}
