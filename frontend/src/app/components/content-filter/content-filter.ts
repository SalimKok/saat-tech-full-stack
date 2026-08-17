import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ContentFilterDto } from '../../models/content-filter';

@Component({
  selector: 'app-content-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './content-filter.html',
  styleUrl: './content-filter.css'
})
export class ContentFilter {
  @Output() filterChanged = new EventEmitter<ContentFilterDto>();

  filter: ContentFilterDto = {
    title: '',
    contentType: '',
    status: 'PUBLISHED',
    genre: '',
    minRating: null,
    year: null
  };

  onSearch(): void {
    this.filterChanged.emit({ ...this.filter });
  }

  onReset(): void {
    this.filter = {
      title: '',
      contentType: '',
      status: 'PUBLISHED',
      genre: '',
      minRating: null,
      year: null
    };
    this.filterChanged.emit({ ...this.filter });
  }

  get isFiltered(): boolean {
    return !!(
      this.filter.title?.trim() ||
      this.filter.contentType ||
      this.filter.status !== 'PUBLISHED' ||
      this.filter.genre?.trim() ||
      (this.filter.minRating && this.filter.minRating > 0) ||
      (this.filter.year && this.filter.year > 0)
    );
  }
}
