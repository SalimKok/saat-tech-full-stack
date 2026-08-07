import { Injectable } from '@angular/core';
import { ContentDto } from '../models/content';


@Injectable({
  providedIn: 'root'
})
export class ContentDetailsService {
 
  sortHierarchy(content: ContentDto): ContentDto {
    if (!content.subContents || content.subContents.length === 0) {
      return content;
    }

    content.subContents.sort((a, b) => (a.seasonNo ?? 0) - (b.seasonNo ?? 0));

    for (const season of content.subContents) {
      if (season.subContents && season.subContents.length > 0) {
        season.subContents.sort((a, b) => (a.episodeNo ?? 0) - (b.episodeNo ?? 0));
      }
    }

    return content;
  }

  getNextSeasonNumber(series: ContentDto): number {
    const seasons = series.subContents ?? [];
    if (seasons.length === 0) return 1;
    const maxSeason = Math.max(...seasons.map(s => s.seasonNo ?? 0));
    return maxSeason + 1;
  }


  getNextEpisodeNumber(season: ContentDto): number {
    const episodes = season.subContents ?? [];
    if (episodes.length === 0) return 1;
    const maxEpisode = Math.max(...episodes.map(e => e.episodeNo ?? 0));
    return maxEpisode + 1;
  }

  buildChildPayload(
    parent: ContentDto,
    contentType: 'SEASON' | 'EPISODE',
    number: number,
    seriesTitle: string = 'Series'
  ): ContentDto {
    const isSeason = contentType === 'SEASON';
    const seasonNo = isSeason ? number : (parent.seasonNo ?? 1);
    const episodeNo = isSeason ? undefined : number;
    const title = isSeason 
      ? `${seriesTitle} - Season ${seasonNo}` 
      : `${seriesTitle} - S${seasonNo}E${episodeNo}`;
    const plot = isSeason 
      ? `Season ${seasonNo} of ${seriesTitle}` 
      : `Season ${seasonNo}, Episode ${episodeNo}`;
    return {
      contentType,
      seasonNo,
      episodeNo,
      parentId: parent.id,
      casts: [],
      metadata: {
        title,
        plot,
        poster: parent.metadata?.poster ?? '',
        genre: parent.metadata?.genre ?? '',
        imdbRating: parent.metadata?.imdbRating ?? 0,
        language: parent.metadata?.language ?? '',
        country: parent.metadata?.country ?? '',
        released: '',
        runtime: '',
        rated: '',
        imdbID: ''
      },
      subContents: []
    };
  }
}
