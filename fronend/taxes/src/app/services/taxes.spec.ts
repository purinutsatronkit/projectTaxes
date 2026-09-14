import { TestBed } from '@angular/core/testing';
import { TaxesService } from './taxes';

describe('Taxes', () => {
  let service: TaxesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaxesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
