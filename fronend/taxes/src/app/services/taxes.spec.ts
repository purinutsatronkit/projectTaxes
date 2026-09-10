import { TestBed } from '@angular/core/testing';
import { Taxes } from './taxes';

describe('Taxes', () => {
  let service: Taxes;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Taxes);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
