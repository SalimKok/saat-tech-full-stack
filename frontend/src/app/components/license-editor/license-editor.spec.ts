import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LicenseEditor } from './license-editor';

describe('LicenseEditor', () => {
  let component: LicenseEditor;
  let fixture: ComponentFixture<LicenseEditor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LicenseEditor],
    }).compileComponents();

    fixture = TestBed.createComponent(LicenseEditor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
