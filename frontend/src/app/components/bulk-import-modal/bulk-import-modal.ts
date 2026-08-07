import { Component, Input, Output, EventEmitter, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OmdbService, BulkImportResponse } from '../../services/omdbService';

@Component({
  selector: 'app-bulk-import-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bulk-import-modal.html',
  styleUrl: './bulk-import-modal.css'
})

export class BulkImportModal {
  private omdbService = inject(OmdbService);
  private cdr = inject(ChangeDetectorRef);

  @Input() isOpen: boolean = false;
  @Output() closed = new EventEmitter<void>();
  @Output() completed = new EventEmitter<BulkImportResponse>();

  bulkIdsText: string = '';
  bulkLoading: boolean = false;
  bulkResult: BulkImportResponse | null = null;
  bulkErrorMessage: string = '';

  close(): void {
    this.closed.emit();
  }

  onExecuteBulkImport(): void {
    const matchedIds = this.bulkIdsText.match(/tt\d+/gi) || [];
    const uniqueIds = Array.from(new Set(matchedIds));

    if (uniqueIds.length === 0) {
      this.bulkErrorMessage = 'Please enter at least one valid IMDb ID (e.g. tt0111161, tt0068646)';
      return;
    }

    this.bulkLoading = true;
    this.bulkErrorMessage = '';
    this.bulkResult = null;
    this.cdr.detectChanges();

    this.omdbService.bulkImport(uniqueIds).subscribe({
      next: (res) => {
        this.bulkResult = res;
        this.bulkLoading = false;
        this.completed.emit(res);
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.bulkLoading = false;
        this.bulkErrorMessage = 'An error occurred during bulk import: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }

 
  clearText(): void {
    this.bulkIdsText = '';
    this.bulkResult = null;
    this.bulkErrorMessage = '';
  }
}
