import { TestBed } from '@angular/core/testing';

import { ContentSearchService } from './content-search-service';

describe('ContentSearchService', () => {
  let service: ContentSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContentSearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
