import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TrailerDto } from '../models/trailer'; 

@Injectable({
  providedIn: 'root'
})

export class TrailerService {
  private http = inject(HttpClient);
  private apiUrl = environment.url + '/contents-trailer';

  fetchTrailers(contentId: number): Observable<TrailerDto[]> {
    return this.http.post<TrailerDto[]>(`${this.apiUrl}/${contentId}/trailers/fetch`, {});
  }

  uploadTrailer(contentId: number, file: File, name: string, type: string): Observable<TrailerDto> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', name);
    formData.append('type', type); 
    
    return this.http.post<TrailerDto>(`${this.apiUrl}/${contentId}/trailers/upload`, formData);
  }

  getSavedTrailers(contentId: number): Observable<TrailerDto[]> {
    return this.http.get<TrailerDto[]>(`${this.apiUrl}/${contentId}/trailers`);
  }
  
  previewTmdbTrailers(contentId: number): Observable<TrailerDto[]> {
    return this.http.get<TrailerDto[]>(`${this.apiUrl}/${contentId}/trailers/tmdb-preview`);
  }
 
  saveTmdbTrailer(contentId: number, trailerData: any): Observable<TrailerDto> {
    return this.http.post<TrailerDto>(`${this.apiUrl}/${contentId}/trailers/tmdb-save`, trailerData);
  }
 
  updateTrailerDetails(contentId: number, trailerId: number, name: string, type: string): Observable<TrailerDto> {
    const payload = { name, type };
    return this.http.patch<TrailerDto>(`${this.apiUrl}/${contentId}/trailers/${trailerId}`, payload);
  }
  
  deleteTrailer(contentId: number, trailerId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${contentId}/trailers/${trailerId}`);
  }

}
