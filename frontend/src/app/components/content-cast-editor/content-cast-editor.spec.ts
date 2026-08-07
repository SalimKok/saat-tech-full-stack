import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentCastEditor } from './content-cast-editor';

describe('ContentCastEditor', () => {
  let component: ContentCastEditor;
  let fixture: ComponentFixture<ContentCastEditor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentCastEditor],
    }).compileComponents();

    fixture = TestBed.createComponent(ContentCastEditor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
