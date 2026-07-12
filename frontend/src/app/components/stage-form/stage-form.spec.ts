import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StageForm } from './stage-form';

describe('StageForm', () => {
  let component: StageForm;
  let fixture: ComponentFixture<StageForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StageForm],
    }).compileComponents();

    fixture = TestBed.createComponent(StageForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
