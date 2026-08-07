import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContentSearchComponent } from './content-search';

describe('ContentSearch', () => {
  let component: ContentSearchComponent;
  let fixture: ComponentFixture<ContentSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentSearchComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentSearchComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
