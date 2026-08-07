import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentDetailModal } from './content-detail-modal';

describe('ContentDetailModal', () => {
  let component: ContentDetailModal;
  let fixture: ComponentFixture<ContentDetailModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentDetailModal],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentDetailModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
