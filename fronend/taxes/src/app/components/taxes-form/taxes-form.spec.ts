import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaxesForm } from './taxes-form';

describe('TaxesForm', () => {
  let component: TaxesForm;
  let fixture: ComponentFixture<TaxesForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaxesForm],
    }).compileComponents();

    fixture = TestBed.createComponent(TaxesForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
