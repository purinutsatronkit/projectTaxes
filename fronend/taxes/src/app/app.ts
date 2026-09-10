import { Component, signal } from '@angular/core';
import { TaxesFormComponent } from './components/taxes-form/taxes-form';

@Component({
  imports: [TaxesFormComponent],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('taxes');
}
