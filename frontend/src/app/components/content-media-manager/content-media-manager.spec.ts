import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentMediaManager } from './content-media-manager';

describe('ContentMediaManager', () => {
  let component: ContentMediaManager;
  let fixture: ComponentFixture<ContentMediaManager>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentMediaManager],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentMediaManager);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
