import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BulkImportModal } from './bulk-import-modal';

describe('BulkImportModal', () => {
  let component: BulkImportModal;
  let fixture: ComponentFixture<BulkImportModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BulkImportModal],
    }).compileComponents();

    fixture = TestBed.createComponent(BulkImportModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
