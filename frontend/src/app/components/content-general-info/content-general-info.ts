import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentService } from '../../services/contentService';

@Component({
  selector: 'app-content-general-info',
  standalone: true,
  imports: [CommonModule],
   templateUrl: './content-general-info.html',
  styleUrls: ['./content-general-info.css']
})
export class ContentGeneralInfoComponent implements OnInit {
  @Input() contentId!: number; 
  contentData: any = null;

  constructor(
    private contentService: ContentService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.contentId) {
      this.fetchContentDetails();
    }
  }

  fetchContentDetails(): void {
    this.contentService.getContentById(this.contentId).subscribe({
      next: (data) => {
        this.contentData = data;
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error('Error fetching content data', err)
    });
  }
}
