import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CastDto } from '../models/cast';
import { PageResponse } from './contentService';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CastService {
  private http = inject(HttpClient);
  private apiUrl = environment.url + '/casts';

  getAllCasts(page = 0, size = 15, name?: string): Observable<PageResponse<CastDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (name?.trim()) {
      params = params.set('name', name.trim());
    }

    return this.http.get<PageResponse<CastDto>>(this.apiUrl, { params });
  }

  saveCast(cast: CastDto): Observable<CastDto> {
    return this.http.post<CastDto>(this.apiUrl, cast);
  }

  updateCast(id: number, cast: CastDto): Observable<CastDto> {
    return this.http.put<CastDto>(`${this.apiUrl}/${id}`, cast);
  }

  deleteCast(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
