import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LicenseService } from '../../services/licenseService';
import { LicenseDto, LicenseStatus } from '../../models/license';

@Component({
  selector: 'app-license-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './license-editor.html',
  styleUrl: './license-editor.css'
})
export class LicenseEditor {
  private licenseService = inject(LicenseService);

  @Input() contentId!: number;
  @Input() licenses: LicenseDto[] = [];
  @Output() licensesChanged = new EventEmitter<LicenseDto[]>();

  newLicense: Partial<LicenseDto> = {
    name: '',
    startDate: '',
    endDate: '',
    status: 'ACTIVE'
  };

  loading: boolean = false;

  addLicense() {
    if (!this.newLicense.name || !this.newLicense.startDate || !this.newLicense.endDate) {
      alert('Please fill all fields!');
      return;
    }

    this.loading = true;
    this.licenseService.addLicenseToContent(this.contentId, this.newLicense as LicenseDto).subscribe({
      next: (savedLicense) => {
        if (!this.licenses) this.licenses = [];
        this.licenses.push(savedLicense);
        this.licensesChanged.emit(this.licenses);
        this.newLicense = { name: '', startDate: '', endDate: '', status: 'ACTIVE' };
        this.loading = false;
      },
      error: (err) => {
        alert('Error adding license!');
        this.loading = false;
      }
    });
  }

  deleteLicense(licenseId: number | undefined) {
    if (!licenseId) return;
    
    if (confirm('Are you sure you want to delete this license?')) {
      this.loading = true;
      this.licenseService.deleteLicense(licenseId).subscribe({
        next: () => {
          this.licenses = this.licenses.filter(l => l.id !== licenseId);
          this.licensesChanged.emit(this.licenses);
          this.loading = false;
        },
        error: () => {
          alert('Error deleting license!');
          this.loading = false;
        }
      });
    }
  }
}
