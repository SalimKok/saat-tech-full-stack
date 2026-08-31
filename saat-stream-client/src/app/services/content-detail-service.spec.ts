import { TestBed } from '@angular/core/testing';

import { ContentDetailService } from './content-detail-service';

describe('ContentDetailService', () => {
  let service: ContentDetailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContentDetailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
