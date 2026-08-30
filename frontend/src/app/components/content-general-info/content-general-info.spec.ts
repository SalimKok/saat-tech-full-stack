import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentGeneralInfo } from './content-general-info';

describe('ContentGeneralInfo', () => {
  let component: ContentGeneralInfo;
  let fixture: ComponentFixture<ContentGeneralInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentGeneralInfo],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentGeneralInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
