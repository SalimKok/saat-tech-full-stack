import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ContentService } from '../../services/contentService';
import { VideoPlayerComponent } from '../video-player/video-player';
import { TrailerService } from '../../services/trailer-service';
import { TrailerDto } from '../../models/trailer';

@Component({
  selector: 'app-content-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, VideoPlayerComponent],
  templateUrl: './content-manager-dashboard.html',
  styleUrls: ['./content-manager-dashboard.css']
})
export class ContentManagerDashboard implements OnInit {
  contentId!: number;
  contentData: any = null;
  activeTab: string = 'media'; 

  activeVideoUrl: string = '';
  activeYoutubeUrl: SafeResourceUrl | null = null;
  
  savedTrailers: TrailerDto[] = [];
  tmdbPreviews: TrailerDto[] = [];
  videoTypes: string[] = ['Trailer', 'Teaser', 'Clip', 'Featurette', 'Behind the Scenes', 'Bloopers'];

  selectedTmdbVideo: any = null;
  pendingLocalFile: File | null = null;

  constructor(
    private route: ActivatedRoute,
    private contentService: ContentService,
    private cdr: ChangeDetectorRef,
    private trailerService: TrailerService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.contentId = +idParam;
        this.fetchContentDetails();
      }
    });
  }

  fetchContentDetails(): void {
    this.contentService.getContentById(this.contentId).subscribe({
      next: (data) => {
        this.contentData = data;
        this.fetchSavedTrailers();
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error fetching content data', err);
      }
    });
  }

  setActiveTab(tab: 'general' | 'media'): void {
    this.activeTab = tab;
    this.cdr.detectChanges();
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
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching TMDB previews', err);
        alert('Failed to fetch TMDB previews!');
      }
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
      
      this.selectedTmdbVideo = {
        name: file.name,
        type: 'Trailer',
        site: 'Local',
        fileUrl: localUrl
      };
      
      setTimeout(() => {
        this.activeVideoUrl = localUrl;
        this.cdr.detectChanges();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }, 50);
    }
  }

  updateSelectedName(event: any): void {
    if (this.selectedTmdbVideo) {
      this.selectedTmdbVideo.name = event.target.value;
    }
  }

  updateSelectedType(event: any): void {
    if (this.selectedTmdbVideo) {
      this.selectedTmdbVideo.type = event.target.value;
    }
  }

  onTypeChange(trailer: any, event: any): void {
    const newType = event.target.value;
    this.trailerService.updateTrailerType(this.contentId, trailer.id, newType).subscribe({
      next: () => console.log('Video type updated successfully'),
      error: (err) => {
        console.error('Error updating type', err);
        alert('Failed to update video type!');
      }
    });
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

    // 1. LOCAL FILE UPLOAD 
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

    // 2. EXISTING DB VIDEO UPDATE (If it has an ID, it means it's already saved)
    if (this.selectedTmdbVideo.id) {
      this.trailerService.updateTrailerType(this.contentId, this.selectedTmdbVideo.id, this.selectedTmdbVideo.type).subscribe({
        next: () => {
          alert('Success! Video tag updated.');
          this.fetchSavedTrailers();
          this.cancelEdit();
        },
        error: (err) => {
          console.error('Update error:', err);
          alert('Failed to update video details!');
        }
      });
      return;
    }

    // 3. TMDB NEW SAVE FLOW (New YouTube video from TMDB)
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
}
