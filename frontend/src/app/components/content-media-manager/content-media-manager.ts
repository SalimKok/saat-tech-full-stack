import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { TrailerService } from '../../services/trailer-service';
import { TrailerDto } from '../../models/trailer'; 
import { VideoPlayerComponent } from '../video-player/video-player';

@Component({
  selector: 'app-content-media-manager',
  standalone: true,
  imports: [CommonModule, VideoPlayerComponent],
  templateUrl: './content-media-manager.html',
  styleUrls: ['./content-media-manager.css']
})
export class ContentMediaManagerComponent implements OnInit {
  @Input() contentId!: number;

  activeVideoUrl: string = '';
  activeYoutubeUrl: SafeResourceUrl | null = null;
  
  savedTrailers: TrailerDto[] = [];
  tmdbPreviews: TrailerDto[] = [];
  videoTypes: string[] = ['Trailer', 'Teaser', 'Clip', 'Featurette', 'Behind the Scenes', 'Bloopers'];

  selectedTmdbVideo: any = null;
  pendingLocalFile: File | null = null;

  currentPage: number = 1;
  itemsPerPage: number = 12;

  constructor(
    private trailerService: TrailerService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.contentId) {
      this.fetchSavedTrailers();
    }
  }

  get paginatedTmdbPreviews(): TrailerDto[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.tmdbPreviews.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.tmdbPreviews.length / this.itemsPerPage);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  fetchSavedTrailers(): void {
    this.trailerService.getSavedTrailers(this.contentId).subscribe({
      next: (data) => {
        this.savedTrailers = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching saved trailers', err)
    });
  }

  fetchTmdbPreviews(): void {
    this.trailerService.previewTmdbTrailers(this.contentId).subscribe({
      next: (data) => {
        this.tmdbPreviews = data;
        this.currentPage = 1;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching TMDB previews', err)
    });
  }

  playSavedVideo(trailer: any): void {
    this.selectedTmdbVideo = null;
    this.pendingLocalFile = null;
    this.activeVideoUrl = '';
    this.activeYoutubeUrl = null;
    
    if (trailer.site === 'Local' && trailer.fileUrl) {
      setTimeout(() => {
        this.activeVideoUrl = trailer.fileUrl;
        this.cdr.detectChanges();
      }, 50);
    } else if (trailer.youtubeEmbedUrl) {
      this.activeYoutubeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(trailer.youtubeEmbedUrl);
      this.cdr.detectChanges();
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  editSavedVideo(trailer: any): void {
    this.pendingLocalFile = null;
    this.activeVideoUrl = '';
    this.selectedTmdbVideo = { ...trailer };
    
    if (trailer.site === 'Local' && trailer.fileUrl) {
      this.activeYoutubeUrl = null;
      setTimeout(() => {
        this.activeVideoUrl = trailer.fileUrl;
        this.cdr.detectChanges();
      }, 50);
    } else if (trailer.youtubeEmbedUrl) {
      this.activeYoutubeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(trailer.youtubeEmbedUrl);
    }
    this.cdr.detectChanges();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  inspectTmdbVideo(trailer: any): void {
    this.pendingLocalFile = null;
    this.activeVideoUrl = ''; 
    this.selectedTmdbVideo = { ...trailer }; 
    if (trailer.youtubeEmbedUrl) {
      this.activeYoutubeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(trailer.youtubeEmbedUrl);
    }
    this.cdr.detectChanges();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.activeYoutubeUrl = null;
      this.activeVideoUrl = '';
      this.pendingLocalFile = file;
      const localUrl = URL.createObjectURL(file);
      this.selectedTmdbVideo = { name: file.name, type: 'Trailer', site: 'Local', fileUrl: localUrl };
      
      setTimeout(() => {
        this.activeVideoUrl = localUrl;
        this.cdr.detectChanges();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }, 50);
    }
  }

  updateSelectedName(event: any): void {
    if (this.selectedTmdbVideo) this.selectedTmdbVideo.name = event.target.value;
  }

  updateSelectedType(event: any): void {
    if (this.selectedTmdbVideo) this.selectedTmdbVideo.type = event.target.value;
  }

  cancelEdit(): void {
    this.selectedTmdbVideo = null;
    this.pendingLocalFile = null;
    this.activeVideoUrl = '';
    this.activeYoutubeUrl = null;
    this.cdr.detectChanges();
  }

  confirmAndSaveTmdbVideo(): void {
    if (!this.selectedTmdbVideo) return;

    if (this.pendingLocalFile) {
      this.trailerService.uploadTrailer(this.contentId, this.pendingLocalFile, this.selectedTmdbVideo.name, this.selectedTmdbVideo.type).subscribe({
        next: () => {
          alert('Success! Local physical video has been saved.');
          this.fetchSavedTrailers();
          this.cancelEdit();
        },
        error: (err) => console.error('Upload error:', err)
      });
      return;
    }

    if (this.selectedTmdbVideo.id) {
      this.trailerService.updateTrailerDetails(
        this.contentId, this.selectedTmdbVideo.id, this.selectedTmdbVideo.name, this.selectedTmdbVideo.type
      ).subscribe({
        next: () => {
          alert('Success! Video details updated.');
          this.fetchSavedTrailers();
          this.cancelEdit();
        },
        error: (err) => console.error('Update error:', err)
      });
      return;
    }

    const payload = {
      name: this.selectedTmdbVideo.name,
      youtubeKey: this.selectedTmdbVideo.youtubeKey,
      site: this.selectedTmdbVideo.site,
      type: this.selectedTmdbVideo.type,
      size: this.selectedTmdbVideo.size,
      language: this.selectedTmdbVideo.language
    };
    
    this.trailerService.saveTmdbTrailer(this.contentId, payload).subscribe({
      next: () => {
        alert('Success! The TMDB Video has been saved.');
        this.fetchSavedTrailers();
        this.tmdbPreviews = this.tmdbPreviews.filter(t => t.youtubeKey !== payload.youtubeKey);
        this.cancelEdit();
      },
      error: (err) => console.error('Error saving TMDB video', err)
    });
  }

  deleteSavedVideo(trailer: any): void {
    const confirmDelete = confirm(`Are you sure you want to completely delete "${trailer.name}"?`);
    if (confirmDelete) {
      this.trailerService.deleteTrailer(this.contentId, trailer.id).subscribe({
        next: () => {
          alert('Video successfully deleted.');
          this.fetchSavedTrailers();
          if (this.selectedTmdbVideo && this.selectedTmdbVideo.id === trailer.id) {
            this.cancelEdit();
          }
        },
        error: (err) => console.error('Delete error', err)
      });
    }
  }
}
