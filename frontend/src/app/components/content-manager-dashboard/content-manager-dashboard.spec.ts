import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentManagerDashboard } from './content-manager-dashboard';

describe('ContentManagerDashboard', () => {
  let component: ContentManagerDashboard;
  let fixture: ComponentFixture<ContentManagerDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentManagerDashboard],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentManagerDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
