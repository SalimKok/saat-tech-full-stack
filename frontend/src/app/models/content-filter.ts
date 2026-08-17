import { ContentType } from './content';

export interface ContentFilterDto {
  title?: string;
  contentType?: ContentType | '';
  status?: 'UNPUBLISHED' | 'PUBLISHED' | 'NO_ACTIVE_LICENSE' | 'DELETED' | '';
  genre?: string;
  minRating?: number | null;
  year?: number | null;
}
