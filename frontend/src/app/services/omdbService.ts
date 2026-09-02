import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContentDto } from '../models/content';
import { environment } from '../../environments/environment';

export interface BulkImportResponse {
  totalRequested: number;
  successCount: number;
  failedCount: number;
  successfulIds: string[];
  failedIds: string[];
}

@Injectable({
  providedIn: 'root'
})
export class OmdbService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  previewContent(imdbId: string): Observable<ContentDto> {
    return this.http.get<ContentDto>(`${this.apiUrl}/omdb/preview?imdbId=${imdbId}`);
  }

  bulkImport(imdbIds: string[]): Observable<BulkImportResponse> {
    return this.http.post<BulkImportResponse>(`${this.apiUrl}/omdb/bulk-import`, imdbIds);
  }
}