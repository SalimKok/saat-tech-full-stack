import { Component, inject, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OmdbService } from '../../services/omdbService';
import { ContentService } from '../../services/contentService';
import { ContentDto } from '../../models/content';
import { BulkImportModal } from '../bulk-import-modal/bulk-import-modal';

@Component({
  selector: 'app-content-search',
  standalone: true,
  imports: [CommonModule, FormsModule, BulkImportModal],
  templateUrl: './content-search.html',
  styleUrl: './content-search.css',
})
export class ContentSearch implements OnInit{

  private omdbService = inject(OmdbService);
  private contentService = inject(ContentService);
  private cdr = inject(ChangeDetectorRef);
  seriesList: ContentDto[] = [];
  seasonsList: ContentDto[] = [];
  autoGenerateTree: boolean = false;
  seasonCount: number = 1;
  seasonEpisodes: number[] = [1]; 

  imdbId: string = ''; 
  fetchLoading: boolean = false;
  saveLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  showBulkModal: boolean = false;

  movie: ContentDto = this.getEmptyMovie();

  onBulkImportCompleted(result: any): void {
    this.loadPotentialParents(); 
  }

  private getEmptyMovie(): ContentDto {
    return {
      contentType: 'MOVIE',
      metadata: {
        title: '',
        poster: '',
        genre: '',
        imdbRating: 0,
        plot: '',
        released: '',
        runtime: '',
        language: '',
        country: '',
        rated: '',
        imdbID: ''
      },
      casts: []
    };
  }

  ngOnInit(): void {
    this.loadPotentialParents();
  }
  loadPotentialParents(): void {
  
    this.contentService.getAllContents(0, 100).subscribe({
      next: (res) => {
        const all = res.content || [];
        this.seriesList = all.filter(c => c.contentType === 'SERIES');
        this.seasonsList = all.filter(c => c.contentType === 'SEASON');
        this.cdr.detectChanges();
      }
    });
  }
  
  onContentTypeChange(type: string): void {
    this.movie.parentId = undefined as any;
    if (type === 'MOVIE' || type === 'SERIES') {
      this.movie.seasonNo = undefined as any;
      this.movie.episodeNo = undefined as any;
    }
  }

    onAutofillFromOmdb(): void {
    if (!this.imdbId.trim()) {
      this.errorMessage = 'Please enter a valid IMDb ID (e.g. tt1375666)';
      return;
    }

    this.fetchLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.omdbService.previewContent(this.imdbId.trim()).subscribe({
      next: (data) => {
        try {
          console.log('OMDb response:', data);
          if (!data) {
            this.errorMessage = 'No data returned from OMDb!';
            return;
          }

          const meta = data.metadata || ({} as any);

          this.movie = {
            contentType: data.contentType || 'MOVIE',
            seasonNo: data.seasonNo,
            episodeNo: data.episodeNo,
            parentId: data.parentId,
            metadata: {
              title: meta.title || '',
              poster: meta.poster || '',
              genre: meta.genre || '',
              imdbRating: meta.imdbRating || 0,
              plot: meta.plot || '',
              released: meta.released ? String(meta.released) : '',
              runtime: meta.runtime || '',
              language: meta.language || '',
              country: meta.country || '',
              rated: meta.rated || '',
              imdbID: meta.imdbID || this.imdbId.trim()
            },
            casts: (data.casts || []).map((c: any) => ({
              castId: c.castId,
              role: c.role,
              castName: c.castName || c.cast?.name || 'Cast Member'
            }))
          };

          this.successMessage = `Form successfully autofilled for "${this.movie.metadata.title}"!`;
        } catch (err) {
          console.error('Error applying OMDb data to form:', err);
          this.errorMessage = 'An error occurred while populating the form.';
        } finally {
          this.fetchLoading = false;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('OMDb HTTP Error:', err);
        this.errorMessage = err.error?.message || err.message || 'Failed to fetch movie from OMDb!';
        this.fetchLoading = false;
        this.cdr.detectChanges();
      }
    });
  }


  onResetForm(): void {
    if (confirm('Are you sure you want to clear the form?')) {
      this.movie = this.getEmptyMovie();
      this.imdbId = '';
      this.errorMessage = '';
      this.successMessage = '';
    }
  }

   onSave(): void {
    if (!this.movie.metadata.title.trim()) {
      this.errorMessage = 'Title is required to save content!';
      return;
    }
    this.saveLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
    let payloadToSave: ContentDto = { ...this.movie };

    if (this.movie.contentType === 'SERIES' && this.autoGenerateTree) {
      const generatedSeasons: any[] = [];
      for (let s = 1; s <= (this.seasonCount || 1); s++) {
        const episodes: any[] = [];
     
        const epCountForThisSeason = this.seasonEpisodes[s - 1] || 1;
        for (let e = 1; e <= epCountForThisSeason; e++) {
          episodes.push({
            contentType: 'EPISODE',
            seasonNo: s,
            episodeNo: e,
            metadata: {
              title: `${this.movie.metadata.title} - S${s}E${e}`,
              poster: this.movie.metadata.poster || '',
              genre: this.movie.metadata.genre || '',
              imdbRating: this.movie.metadata.imdbRating || 0,
              plot: `Episode ${e} of Season ${s}`,
              released: '',
              runtime: '',
              language: this.movie.metadata.language || '',
              country: this.movie.metadata.country || '',
              rated: '',
              imdbID: ''
            }
          });
        }
        generatedSeasons.push({
          contentType: 'SEASON',
          seasonNo: s,
          metadata: {
            title: `${this.movie.metadata.title} - Season ${s}`,
            poster: this.movie.metadata.poster || '',
            genre: this.movie.metadata.genre || '',
            imdbRating: this.movie.metadata.imdbRating || 0,
            plot: `Season ${s} of ${this.movie.metadata.title}`,
            released: '',
            runtime: '',
            language: this.movie.metadata.language || '',
            country: this.movie.metadata.country || '',
            rated: '',
            imdbID: ''
          },
          subContents: episodes
        });
      }
      payloadToSave.subContents = generatedSeasons;
    }
    this.contentService.saveContent(payloadToSave).subscribe({
      next: (savedData) => {
        try {
          const title = savedData?.metadata?.title || payloadToSave.metadata.title || 'Content';
          const totalCreated = this.autoGenerateTree 
            ? `"${title}" with ${this.seasonCount} Season(s) and ${this.getTotalEpisodes()} Episodes` 
            : `"${title}"`;
            
          this.successMessage = `Successfully created: ${totalCreated}!`;
          this.movie = this.getEmptyMovie();
          this.autoGenerateTree = false;
          this.seasonCount = 1;
          this.seasonEpisodes = [1];
          this.imdbId = '';
          this.loadPotentialParents();
        } catch (err) {
          console.error('Error handling save response:', err);
          this.successMessage = 'Content saved successfully!';
        } finally {
          this.saveLoading = false;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Save error:', err);
        this.errorMessage = err.error?.message || 'An error occurred while saving!';
        this.saveLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  
  onSeasonCountChange(): void {
    const count = Math.max(1, Math.min(50, this.seasonCount || 1));
    this.seasonCount = count;
    while (this.seasonEpisodes.length < count) {
      this.seasonEpisodes.push(1); // Yeni eklenen sezon için varsayılan 1 bölüm
    }
    if (this.seasonEpisodes.length > count) {
      this.seasonEpisodes = this.seasonEpisodes.slice(0, count);
    }
  }

  getTotalEpisodes(): number {
    return this.seasonEpisodes.reduce((sum, val) => sum + (Number(val) || 0), 0);
  }
  
}
