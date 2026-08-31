import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentDetailComponent } from './content-detail';

describe('ContentDetail', () => {
  let component: ContentDetailComponent;
  let fixture: ComponentFixture<ContentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
