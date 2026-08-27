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
}
