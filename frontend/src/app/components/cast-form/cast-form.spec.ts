import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CastForm } from './cast-form';

describe('CastForm', () => {
  let component: CastForm;
  let fixture: ComponentFixture<CastForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CastForm],
    }).compileComponents();

    fixture = TestBed.createComponent(CastForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
