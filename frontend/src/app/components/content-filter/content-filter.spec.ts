import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentFilter } from './content-filter';

describe('ContentFilter', () => {
  let component: ContentFilter;
  let fixture: ComponentFixture<ContentFilter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentFilter],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentFilter);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
