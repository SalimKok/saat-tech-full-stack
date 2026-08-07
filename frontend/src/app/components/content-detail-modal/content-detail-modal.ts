import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  inject,
  ChangeDetectorRef,
  DestroyRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ContentDto } from '../../models/content';
import { ContentService } from '../../services/contentService';
import { ContentDetailsService } from '../../services/contentDetailsService';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-content-detail-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './content-detail-modal.html',
  styleUrl: './content-detail-modal.css',
})
export class ContentDetailModal implements OnChanges {
  @Input() contentId: number | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() editRequested = new EventEmitter<ContentDto>();
  @Output() contentChanged = new EventEmitter<void>();


  private readonly contentService = inject(ContentService);
  private readonly contentDetailsService = inject(ContentDetailsService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  
  content: ContentDto | null = null;
  selectedSeason: ContentDto | null = null;
  loading: boolean = false;
  errorMessage: string = '';
  actionMessage: string = '';

  
  isAddingSeason: boolean = false;
  inputSeasonNo: number = 1;
  isAddingEpisode: boolean = false;
  inputEpisodeNo: number = 1;
  inputEpisodeTitle: string = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['contentId']) {
      if (this.contentId) {
        this.fetchContentHierarchy(this.contentId);
      } else {
        this.resetState();
      }
    }
  }

  fetchContentHierarchy(id: number, preserveSeasonNo?: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.actionMessage = '';
    this.cdr.markForCheck();

    this.contentService.getContentById(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.content = this.contentDetailsService.sortHierarchy(data);
          this.resolveActiveSeason(preserveSeasonNo);
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.message ?? 'Failed to load content details';
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }

  private resolveActiveSeason(preserveSeasonNo?: number): void {
    if (this.content?.contentType !== 'SERIES' || !this.content.subContents?.length) {
      this.selectedSeason = null;
      return;
    }

    if (preserveSeasonNo !== undefined) {
      this.selectedSeason = this.content.subContents.find(s => s.seasonNo === preserveSeasonNo) 
        ?? this.content.subContents[0];
    } else {
      this.selectedSeason = this.content.subContents[0];
    }
  }

  selectSeason(season: ContentDto): void {
    this.selectedSeason = season;
    this.cdr.markForCheck();
  }

  onEdit(item: ContentDto): void {
    this.editRequested.emit(item);
  }

  onDeleteSubContent(item: ContentDto, entityLabel: string): void {
    if (!item.id || !this.content?.id) return;

    const title = item.metadata?.title ?? `${entityLabel} ${item.episodeNo ?? item.seasonNo ?? ''}`;
    if (!confirm(`"${title}" Are you sure you want to delete?`)) return;

    const currentSeasonNo = this.selectedSeason?.seasonNo;

    this.contentService.deleteContent(item.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.actionMessage = `${entityLabel} deleted successfully`;
          this.fetchContentHierarchy(this.content!.id!, currentSeasonNo);
          this.contentChanged.emit(); 
        },
        error: (err) => {
          alert(`Failed to delete ${entityLabel}: ${err.error?.message ?? 'Server error'}`);
        }
      });
  }

  
  openAddSeasonForm(): void {
    if (!this.content) return;
    this.inputSeasonNo = this.contentDetailsService.getNextSeasonNumber(this.content);
    this.isAddingSeason = true;
  }
  cancelAddSeason(): void {
    this.isAddingSeason = false;
  }
  submitAddSeason(): void {
    if (!this.content?.id || !this.inputSeasonNo || this.inputSeasonNo <= 0) {
      alert('Please enter a valid season number!');
      return;
    }
    const seasonNo = Number(this.inputSeasonNo);
    const payload = this.contentDetailsService.buildChildPayload(this.content, 'SEASON',  seasonNo, this.content.metadata?.title);
    this.contentService.saveContent(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.actionMessage = `Season ${seasonNo} created / restored successfully.`;
          this.isAddingSeason = false;
          this.fetchContentHierarchy(this.content!.id!, seasonNo);
          this.contentChanged.emit();
        },
        error: (err) => {
          alert(`Failed to add season: ${err.error?.message ?? 'Server error'}`);
        }
      });
  }

  openAddEpisodeForm(season: ContentDto): void {
    this.inputEpisodeNo = this.contentDetailsService.getNextEpisodeNumber(season);
    this.inputEpisodeTitle = '';
    this.isAddingEpisode = true;
  }
  cancelAddEpisode(): void {
    this.isAddingEpisode = false;
  }
  submitAddEpisode(season: ContentDto): void {
    if (!season.id || !this.content?.id || !this.inputEpisodeNo || this.inputEpisodeNo <= 0) {
      alert('Please enter a valid episode number!');
      return;
    }
    const epNo = Number(this.inputEpisodeNo);
    const payload = this.contentDetailsService.buildChildPayload(season,'EPISODE', epNo, this.content?.metadata?.title);
    if (this.inputEpisodeTitle.trim()) {
      payload.metadata.title = this.inputEpisodeTitle.trim();
    }
    this.contentService.saveContent(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.actionMessage = `Episode ${epNo} added / restored successfully.`;
          this.isAddingEpisode = false;
          this.fetchContentHierarchy(this.content!.id!, season.seasonNo);
          this.contentChanged.emit();
        },
        error: (err) => {
          alert(`Failed to add episode: ${err.error?.message ?? 'Server error'}`);
        }
      });
  }

  onDeleteMain(content: ContentDto): void {
    if (!content.id) return;
    const title = content.metadata?.title ?? 'this content';
    if (!confirm(`Are you sure you want to delete "${title}"?`)) {
      return;
    }
    this.contentService.deleteContent(content.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.contentChanged.emit();
          this.close();
        },
        error: (err) => {
          alert(`Failed to delete content: ${err.error?.message ?? 'Server error'}`);
        }
      });
  }

  close(): void {
    this.closed.emit();
  }

  private resetState(): void {
    this.content = null;
    this.selectedSeason = null;
    this.errorMessage = '';
    this.actionMessage = '';
  }
}
