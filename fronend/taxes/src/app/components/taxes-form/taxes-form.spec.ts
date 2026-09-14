import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaxesFormComponent } from './taxes-form';

describe('TaxesForm', () => {
  let component: TaxesFormComponent;
  let fixture: ComponentFixture<TaxesFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaxesFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TaxesFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
