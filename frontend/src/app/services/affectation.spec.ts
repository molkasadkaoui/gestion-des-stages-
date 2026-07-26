import { TestBed } from '@angular/core/testing';

import { Affectation } from './affectation';

describe('Affectation', () => {
  let service: Affectation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Affectation);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
