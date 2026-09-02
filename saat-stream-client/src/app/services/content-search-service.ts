import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContentIndex, PageResponse, SearchFilter } from '../models/content-index';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ContentSearchService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/contentSearch`;

  search(filter: SearchFilter): Observable<PageResponse<ContentIndex>> {
    let params = new HttpParams()
      .set('page', (filter.page ?? 0).toString())
      .set('size', (filter.size ?? 40).toString());

    if (filter.query?.trim()) {
      params = params.set('query', filter.query.trim());
    }
    if (filter.contentType) {
      params = params.set('contentType', filter.contentType);
    }
    if (filter.genre && filter.genre !== 'All') {
      params = params.set('genre', filter.genre.trim());
    }
    if (filter.minRating) {
      params = params.set('minRating', filter.minRating.toString());
    }
    if (filter.year) {
      params = params.set('year', filter.year.toString());
    }

    if (filter.titleBoost !== undefined) {
      params = params.set('titleBoost', filter.titleBoost.toString());
    }
    if (filter.plotBoost !== undefined) {
      params = params.set('plotBoost', filter.plotBoost.toString());
    }
    if (filter.castBoost !== undefined) {
      params = params.set('castBoost', filter.castBoost.toString());
    }
    if (filter.genreBoost !== undefined) {
      params = params.set('genreBoost', filter.genreBoost.toString());
    }
    
    if (filter.bm25Weight !== undefined) {
      params = params.set('bm25Weight', filter.bm25Weight.toString());
    }
    if (filter.vectorWeight !== undefined) {
      params = params.set('vectorWeight', filter.vectorWeight.toString());
    }
    return this.http.get<PageResponse<ContentIndex>>(`${this.baseUrl}/search`, { params });
  }

  syncElasticsearch(): Observable<string> {
    return this.http.post(`${this.baseUrl}/sync-elasticsearch`, {}, { responseType: 'text' });
  }
}
