import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CastDetail } from './cast-detail';

describe('CastDetail', () => {
  let component: CastDetail;
  let fixture: ComponentFixture<CastDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CastDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(CastDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
