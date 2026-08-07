import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CastManagement } from './cast-management';

describe('CastManagement', () => {
  let component: CastManagement;
  let fixture: ComponentFixture<CastManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CastManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(CastManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
