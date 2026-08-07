import { ContentType } from './content';

export interface ContentFilterDto {
  title?: string;
  contentType?: ContentType | '';
  status?: 'ACTIVE' | 'DELETED' | '';
  genre?: string;
  minRating?: number | null;
  year?: number | null;
}
