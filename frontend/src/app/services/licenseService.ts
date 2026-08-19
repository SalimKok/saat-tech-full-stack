import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LicenseDto } from '../models/license';

@Injectable({
  providedIn: 'root'
})
export class LicenseService {
  private http = inject(HttpClient);
  private apiUrl = '/api/licenses';

  addLicenseToContent(contentId: number, data: LicenseDto): Observable<LicenseDto> {
    return this.http.post<LicenseDto>(`${this.apiUrl}/content/${contentId}`, data);
  }

  updateLicense(licenseId: number, data: LicenseDto): Observable<LicenseDto> {
    return this.http.put<LicenseDto>(`${this.apiUrl}/${licenseId}`, data);
  }

  detachLicense(licenseId: number): Observable<LicenseDto> {
    return this.http.put<LicenseDto>(`${this.apiUrl}/${licenseId}/detach`, {});
  }

  deleteLicense(licenseId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${licenseId}`);
  }
}
