export interface MatchExplanation {
  totalScore?: number;
  titleScore?: number;
  plotScore?: number;
  castScore?: number;
  genreScore?: number;
  matchedFields?: string[];
  highlightedSnippets?: { [field: string]: string[] };
  termFrequencies?: { [field: string]: number };
  passedFilters?: string[];
  decisionSummary?: string;
  functionalReason?: string;
  bm25Score?: number;
  semanticSimilarityScore?: number;
  isSemanticMatch?: boolean;
  bm25WeightUsed?: number;
  vectorWeightUsed?: number;
}
export interface ContentIndex {
  id: number;
  title: string;
  plot: string;
  genre: string;
  castNames: string[];
  contentType: 'MOVIE' | 'SERIES' | 'SEASON' | 'EPISODE';
  imdbRating?: number;
  year?: number;
  runtimeMinutes?: number;
  poster?: string;
  score?: number;
  matchExplanation?: MatchExplanation;
}

export interface SearchFilter {
  query?: string;
  contentType?: string;
  genre?: string;
  minRating?: number;
  year?: number;
  page?: number;
  size?: number;  
  titleBoost?: number;
  plotBoost?: number;
  castBoost?: number;
  genreBoost?: number;
  bm25Weight?: number;
  vectorWeight?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
