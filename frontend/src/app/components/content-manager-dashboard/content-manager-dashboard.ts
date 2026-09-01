import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ContentMediaManagerComponent } from '../content-media-manager/content-media-manager';
import { ContentGeneralInfoComponent } from '../content-general-info/content-general-info';
import { ContentService } from '../../services/contentService';
import { EditModal } from '../edit-modal/edit-modal';

@Component({
  selector: 'app-content-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, ContentMediaManagerComponent, ContentGeneralInfoComponent, EditModal], 
  templateUrl: './content-manager-dashboard.html',
  styleUrls: ['./content-manager-dashboard.css']
})
export class ContentManagerDashboard implements OnInit {
  contentId!: number;
  activeTab: string = 'general';
  
  selectedMovie: any = null; 

  constructor(
    private route: ActivatedRoute,
    private router: Router, 
    private cdr: ChangeDetectorRef,
    private contentService: ContentService 
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.contentId = +idParam;
        this.cdr.detectChanges();
      }
    });
  }

  setActiveTab(tab: 'general' | 'media'): void {
    this.activeTab = tab;
    this.cdr.detectChanges();
  }

  deleteContent(): void {
    const confirmDelete = confirm('Are you sure you want to permanently delete this content?');
    if (confirmDelete) {
      this.contentService.deleteContent(this.contentId).subscribe({
        next: () => {
          alert('Content successfully deleted.');
          this.router.navigate(['/contents']); 
        },
        error: (err) => {
          console.error('Delete error', err);
          alert('Failed to delete content.');
        }
      });
    }
  }

  openEditModal(): void {
    this.contentService.getContentById(this.contentId).subscribe({
      next: (data) => {
        this.selectedMovie = data;
        this.cdr.detectChanges();
      },
      error: (err) => alert('Error loading content for edit.')
    });
  }

  onModalClosed(): void {
    this.selectedMovie = null;
    this.cdr.detectChanges();
  }

  onMovieSaved(): void {
    this.selectedMovie = null;
    alert('Changes saved successfully!');
    window.location.reload(); 
  }
}
