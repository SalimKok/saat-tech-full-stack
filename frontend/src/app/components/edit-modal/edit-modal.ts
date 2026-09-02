import { Component, Input, Output, EventEmitter, inject, ChangeDetectorRef, OnChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ContentService } from '../../services/contentService';
import { ContentDto } from '../../models/content';
import { ContentCastEditor } from '../content-cast-editor/content-cast-editor';
import { LicenseEditor } from '../license-editor/license-editor';
import { AlertService } from '../../services/alert-service';


@Component({
  selector: 'app-edit-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ContentCastEditor, LicenseEditor],
  templateUrl: './edit-modal.html',
  styleUrl: './edit-modal.css'
})
export class EditModal implements OnChanges, OnDestroy {
  private contentService = inject(ContentService);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  constructor(
      private alertService: AlertService
    ) {}

  @Input() movie: ContentDto | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  editingMovie: ContentDto | null = null;
  editLoading: boolean = false;
  editErrorMessage: string = '';

  updateChildren: boolean = false;

  ngOnChanges(): void {
    if (this.movie) {
      this.editingMovie = JSON.parse(JSON.stringify(this.movie));
      this.editErrorMessage = '';
      this.editLoading = false;
      this.updateChildren = false; 
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onContentTypeChange(newType: string): void {
    if (!this.editingMovie) return;
    if (newType !== 'SEASON' && newType !== 'EPISODE') {
      this.editingMovie.seasonNo = undefined as any;
      this.editingMovie.episodeNo = undefined as any;
    } else if (newType === 'SEASON') {
      this.editingMovie.episodeNo = undefined as any;
    }
  }

  close(): void {
    this.closed.emit();
  }

  onStatusChange(newStatus: string): void {
    if (!this.editingMovie || !this.editingMovie.id) return;
    if (this.editingMovie.status === newStatus) return;
    
    this.editLoading = true;
    const previousStatus = this.editingMovie.status; 
    
    this.contentService.changeContentStatus(this.editingMovie.id, newStatus)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedContent) => {
          this.editingMovie!.status = updatedContent.status;
          this.editLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.alertService.showError(err.error?.message || 'The status hasnt changed! Make sure you have an active license.');

          this.editingMovie!.status = undefined as any;
          this.cdr.detectChanges();
          this.editingMovie!.status = previousStatus;
          
          this.editLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  save(): void {
    if (!this.editingMovie || !this.editingMovie.id) return;

    this.editLoading = true;
    this.editErrorMessage = '';
    this.cdr.detectChanges();

    const payload = {
      contentType: this.editingMovie.contentType,
      seasonNo: this.editingMovie.seasonNo,
      episodeNo: this.editingMovie.episodeNo,
      parentId: this.editingMovie.parentId,
      metadata: {
        title: this.editingMovie.metadata.title,
        poster: this.editingMovie.metadata.poster,
        plot: this.editingMovie.metadata.plot,
        imdbRating: Number(this.editingMovie.metadata.imdbRating) || 0,
        genre: this.editingMovie.metadata.genre,
        language: this.editingMovie.metadata.language,
        country: this.editingMovie.metadata.country,
        released: this.editingMovie.metadata.released,
        runtime: this.editingMovie.metadata.runtime,
        imdbVotes: this.editingMovie.metadata.imdbVotes,
        imdbID: this.editingMovie.metadata.imdbID
      },
  
      casts: this.editingMovie.casts?.map(c => ({
        castId: c.cast?.id,
        role: c.role
      })) || []
    };

    this.contentService.updateContent(this.editingMovie.id, payload as any, this.updateChildren)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.editLoading = false;
          this.saved.emit();
        },
        error: (err) => {
          this.alertService.showError(err.error?.message || 'Failed to update movie!');
          this.editLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

}
