import { Directive, HostListener, ElementRef } from '@angular/core';

@Directive({
  selector: '[appTaxIdMask]',
  standalone: true
})
export class TaxIdMaskDirective {

  constructor(private el: ElementRef) {}

  @HostListener('input', ['$event'])
  onInputChange(event: Event) {
    const input = this.el.nativeElement as HTMLInputElement;
    
    // 1. กรองเอาเฉพาะตัวเลข 0-9
    let sanitizedValue = input.value.replace(/[^0-9]/g, '');

    // 2. ตัดความยาวไม่ให้เกิน 13 หลัก
    if (sanitizedValue.length > 13) {
      sanitizedValue = sanitizedValue.substring(0, 13);
    }

    // 3. ยัดค่ากลับลงไปใน Input
    input.value = sanitizedValue;
  }

  // ป้องกันการพิมพ์ตัวอักษรพิเศษหรือตัวหนังสือตั้งแต่แป้นพิมพ์ (กด Keydown)
  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    const allowedKeys = [
      'Backspace', 'Tab', 'End', 'Home', 'ArrowLeft', 'ArrowRight', 'Delete'
    ];

    if (allowedKeys.indexOf(event.key) !== -1 ||
      (event.key === 'a' && event.ctrlKey === true) || // Ctrl+A
      (event.key === 'c' && event.ctrlKey === true) || // Ctrl+C
      (event.key === 'v' && event.ctrlKey === true) || // Ctrl+V
      (event.key === 'x' && event.ctrlKey === true)    // Ctrl+X
    ) {
      return;
    }

    // ยอมรับเฉพาะตัวเลข 0-9
    if (['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'].indexOf(event.key) === -1) {
      event.preventDefault();
    }
  }
}