import { Component, Input, Output, EventEmitter, inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ContentService } from '../../services/contentService';
import { CastService } from '../../services/castservice';
import { CastDto } from '../../models/cast';
import { ContentCastDto, CastType } from '../../models/content-cast';

@Component({
  selector: 'app-content-cast-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './content-cast-editor.html',
  styleUrl: './content-cast-editor.css'
})
export class ContentCastEditor implements OnInit, OnDestroy {
  private contentService = inject(ContentService);
  private castService = inject(CastService);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  @Input() contentId!: number;
  @Input() casts: ContentCastDto[] = [];
  @Output() castsChange = new EventEmitter<ContentCastDto[]>();

  allCasts: CastDto[] = [];
  selectedExistingCastId: number | null = null;
  newCastName: string = '';
  selectedRole: CastType = 'ACTOR';
  actionLoading: boolean = false;
  errorMessage: string = '';

  ngOnInit(): void {
    this.loadAllCasts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

    loadAllCasts(): void {
    this.castService.getAllCasts(0, 100)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.allCasts = res.content || [];
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Failed to load casts', err)
      });
  }


  onRemoveCast(castId: number | undefined): void {
    if (!this.contentId || !castId) return;

    this.actionLoading = true;
    this.errorMessage = '';

    this.contentService.removeCastFromContent(this.contentId, castId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const updatedCasts = (this.casts || []).filter(c => c.cast?.id !== castId);
          this.casts = updatedCasts;
          this.castsChange.emit(this.casts);
          this.actionLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to remove cast!';
          this.actionLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  onAddCast(): void {
    if (!this.contentId) return;

    if (this.selectedExistingCastId) {
      this.attachCast(this.selectedExistingCastId, this.selectedRole);
      return;
    }

    if (this.newCastName.trim()) {
      this.actionLoading = true;
      this.errorMessage = '';

      this.castService.saveCast({ name: this.newCastName.trim() })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (savedCast) => {
            this.allCasts.push(savedCast);
            this.newCastName = '';
            if (savedCast.id) {
              this.attachCast(savedCast.id, this.selectedRole);
            }
          },
          error: (err) => {
            this.errorMessage = err.error?.message || 'Failed to create new cast!';
            this.actionLoading = false;
            this.cdr.detectChanges();
          }
        });
    }
  }

  private attachCast(castId: number, role: CastType): void {
    this.actionLoading = true;
    this.errorMessage = '';

    this.contentService.addCastToContent(this.contentId, castId, role)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          const person = this.allCasts.find(c => c.id === Number(castId)) || { id: castId, name: 'Unknown' };
          const updatedCasts = [...(this.casts || [])];
          updatedCasts.push({
            cast: person,
            role: role
          });
          this.casts = updatedCasts;
          this.castsChange.emit(this.casts);

          this.selectedExistingCastId = null;
          this.actionLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to link cast to movie!';
          this.actionLoading = false;
          this.cdr.detectChanges();
        }
      });
  }
}
