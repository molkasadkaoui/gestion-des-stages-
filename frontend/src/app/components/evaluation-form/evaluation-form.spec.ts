import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluationForm } from './evaluation-form';

describe('EvaluationForm', () => {
  let component: EvaluationForm;
  let fixture: ComponentFixture<EvaluationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvaluationForm],
    }).compileComponents();

    fixture = TestBed.createComponent(EvaluationForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
