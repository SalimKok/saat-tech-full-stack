import { CastDto } from './cast';

export type CastType = 'ACTOR' | 'DIRECTOR' | 'WRITER';

export interface ContentCastDto {
  id?: number;
  cast?: CastDto; 
  castName?: string; 
  contentId?: number;
  castId?: number;
  role: CastType;
}
