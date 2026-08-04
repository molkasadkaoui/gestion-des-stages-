import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonAffectation } from './mon-affectation';

describe('MonAffectation', () => {
  let component: MonAffectation;
  let fixture: ComponentFixture<MonAffectation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonAffectation],
    }).compileComponents();

    fixture = TestBed.createComponent(MonAffectation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
