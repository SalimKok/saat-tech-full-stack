import { TestBed } from '@angular/core/testing';

import { ContentDetailsService } from './contentDetailsService';

describe('ContentDetails', () => {
  let service: ContentDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContentDetailsService);  
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
