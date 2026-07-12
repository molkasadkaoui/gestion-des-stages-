import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StageList } from './stage-list';

describe('StageList', () => {
  let component: StageList;
  let fixture: ComponentFixture<StageList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StageList],
    }).compileComponents();

    fixture = TestBed.createComponent(StageList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
