import { TestBed } from '@angular/core/testing';

import { OmdbService } from './omdbService';

describe('Omdb', () => {
  let service: OmdbService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OmdbService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
