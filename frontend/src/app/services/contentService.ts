import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContentDto } from '../models/content';
import { CastType } from '../models/content-cast';
import { environment } from '../../environments/environment';
import { ContentFilterDto } from '../models/content-filter';


export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})

export class ContentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/contents`;


  getAllContents(page = 0, size = 20, filter?: ContentFilterDto): Observable<PageResponse<ContentDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (filter?.title?.trim()) {
      params = params.set('title', filter.title.trim());
    }
    if (filter?.contentType?.trim()) {
      params = params.set('contentType', filter.contentType.trim());
    }
    if (filter?.status?.trim()) {
      params = params.set('status', filter.status.trim());
    }
    if (filter?.genre?.trim()) {
      params = params.set('genre', filter.genre.trim());
    }
    if (filter?.minRating !== undefined && filter?.minRating !== null && filter.minRating > 0) {
      params = params.set('minRating', filter.minRating.toString());
    }
    if (filter?.year !== undefined && filter?.year !== null && filter.year > 0) {
      params = params.set('year', filter.year.toString());
    }
    return this.http.get<PageResponse<ContentDto>>(this.apiUrl, { params });
  }

 getContentById(id: number): Observable<ContentDto> {
    return this.http.get<ContentDto>(`${this.apiUrl}/${id}`);
  }

  saveContent(content: ContentDto): Observable<ContentDto> {
    return this.http.post<ContentDto>(this.apiUrl, content);
  }

  deleteContent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateContent(id: number, content: ContentDto, updateChildren: boolean = false): Observable<ContentDto> {
    const params = new HttpParams().set('updateChildren', updateChildren.toString());
    return this.http.put<ContentDto>(`${this.apiUrl}/${id}`, content, { params });
  }

  addCastToContent(contentId: number, castId: number, role: CastType): Observable<void> {
    const params = new HttpParams().set('role', role);
    return this.http.post<void>(`${this.apiUrl}/${contentId}/casts/${castId}`, null, { params });
  }
  
  removeCastFromContent(contentId: number, castId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${contentId}/casts/${castId}`);
  }

  changeContentStatus(id: number, status: string): Observable<ContentDto> {
    const params = new HttpParams().set('status', status);
    return this.http.put<ContentDto>(`${this.apiUrl}/${id}/status`, null, { params });
  }
}
