import { MetadataDto } from './metadata';
import { ContentCastDto } from './content-cast';

export type ContentType = 'MOVIE' | 'SERIES' | 'EPISODE' | 'SEASON';


export interface ContentDto {
  id?: number;
  seasonNo?: number;
  episodeNo?: number;
  contentType: ContentType;
  parentId?: number;
  casts: ContentCastDto[];
  metadata: MetadataDto;
   subContents?: ContentDto[];
}
