import { Component, inject, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CastService } from '../../services/castservice';
import { CastContentDto } from '../../models/CastContentDto';

@Component({
  selector: 'app-cast-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cast-edit.html',
  styleUrl: './cast-edit.css'
})
export class CastEdit implements OnInit {
  private castService = inject(CastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef); 

  isEditMode: boolean = false;
  castId: number | null = null;

  name: string = '';
  
  inputType: 'UPLOAD' | 'URL' = 'UPLOAD';
  posterUrl: string = '';
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  isSubmitting: boolean = false;
  isDeleting: boolean = false;
  errorMessage: string = '';
  
  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.castId = Number(idParam);
      this.loadCastData(this.castId);
    }
  }

  loadCastData(id: number): void {
    this.castService.getCastById(id).subscribe({
      next: (cast) => {
        this.name = cast.name;
        if (cast.poster) {
          this.posterUrl = cast.poster;
          this.imagePreview = cast.poster;
          this.inputType = 'URL';
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = "Failed to load cast data.";
        this.cdr.detectChanges();
      }
    });
  }

  setUploadMode(): void {
    this.inputType = 'UPLOAD';
    this.posterUrl = '';
  }

  setUrlMode(): void {
    this.inputType = 'URL';
    this.removeImage(); 
  }

  onPosterUrlChange(): void {
    this.imagePreview = this.posterUrl;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.handleFile(file);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('image/')) {
        this.handleFile(file);
      } else {
        this.errorMessage = "Please upload an image file.";
      }
    }
  }

  private handleFile(file: File): void {
    this.selectedFile = file;
    this.posterUrl = '';
    this.errorMessage = '';
    
    const reader = new FileReader();
    reader.onload = e => {
      this.imagePreview = reader.result;
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.selectedFile = null;
    this.imagePreview = null;
    this.cdr.detectChanges();
  }

  onSubmit(): void {
    if (!this.name.trim()) {
      this.errorMessage = "Please enter a name.";
      return;
    }
    
    this.isSubmitting = true;
    this.errorMessage = '';
    this.cdr.detectChanges(); 

    if (this.inputType === 'UPLOAD' && this.selectedFile) {
      this.castService.uploadPoster(this.selectedFile).subscribe({
        next: (res) => {
          this.saveCastData(res.url);
        },
        error: (err) => {
          console.error("Image upload failed:", err);
          this.errorMessage = "Failed to upload image.";
          this.isSubmitting = false;
          this.cdr.detectChanges(); 
        }
      });
    } else {
      this.saveCastData(this.posterUrl);
    }
  }

  private saveCastData(finalPosterUrl: string): void {
    const castData = { name: this.name.trim(), poster: finalPosterUrl };

    const request = this.isEditMode && this.castId
      ? this.castService.updateCast(this.castId, castData)
      : this.castService.saveCast(castData);

    request.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/casts']); 
      },
      error: (err) => {
        console.error("Save cast failed:", err);
        this.errorMessage = "Failed to save cast.";
        this.isSubmitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteCast(): void {
    if (confirm('Are you sure you want to delete this cast member?')) {
      if (this.castId) {
        this.isDeleting = true;
        this.castService.deleteCast(this.castId).subscribe({
          next: () => {
            this.router.navigate(['/casts']);
          },
          error: (err) => {
            console.error("Delete cast failed:", err);
            this.errorMessage = "Failed to delete cast.";
            this.isDeleting = false;
            this.cdr.detectChanges();
          }
        });
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/casts']);
  }
}
