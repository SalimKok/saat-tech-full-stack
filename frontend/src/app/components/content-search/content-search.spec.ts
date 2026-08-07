import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentSearch } from './content-search';

describe('ContentSearch', () => {
  let component: ContentSearch;
  let fixture: ComponentFixture<ContentSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentSearch],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentSearch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
