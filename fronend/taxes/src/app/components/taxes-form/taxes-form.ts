import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaxesService } from '../../services/taxes';
import { TaxesHeader, TaxesDetail, TaxRateResponse } from '../../models/taxes';
import { FormsModule } from '@angular/forms';
import { TaxIdMaskDirective } from '../../directives/tax-id-mask';

@Component({
  selector: 'app-taxes-form',
  standalone: true,
  imports: [CommonModule, FormsModule, TaxIdMaskDirective],
  templateUrl: './taxes-form.html',
  styleUrls: ['./taxes-form.scss'],
})
export class TaxesFormComponent implements OnInit {
  detailForm: TaxesDetail = this.getEmptyDetail();
  detailList: TaxesDetail[] = [];

  // ตัวแปรสำหรับระบุตำแหน่ง -1 = edit
  editingIndex: number = -1;

  // ยอด
  totalPurchase = 0;
  totalVat = 0;
  totalRefund = 0;
  totalRefundAg = 0;
  totalFee = 0;

  // ค้นหา
  searchSummaryNo = '';
  searchSummaryDate = '';
  currentSummaryNo = '';

  // Modal
  showSelectSummaryModal: boolean = false;
  matchedSummaryList: TaxesHeader[] = [];

  constructor(
    private taxesService: TaxesService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {}

  // สร้าง Object เปล่าสำหรับฟอร์มกรอกข้อมูล
  getEmptyDetail(): TaxesDetail {
    return {
      bookNo: '',
      docNo: '',
      docDate: new Date().toISOString().split('T')[0],
      taxId: '',
      branchNo: '',
      companyName: '',
      purchaseAmount: 0,
      vatAmount: 0,
      refundRevenue: 0,
      refundAgent: 0,
      feeAmount: 0,
    };
  }

  // รีเซ็ตฟอร์ม
  resetDetailForm(): void {
    this.detailForm = this.getEmptyDetail();
  }

  // คำนวณอัตราภาษี
  onPurchaseAmountChange(): void {
    const amount = Number(this.detailForm.purchaseAmount) || 0;
    if (amount <= 0) {
      this.detailForm.vatAmount = 0;
      this.detailForm.refundRevenue = 0;
      this.detailForm.refundAgent = 0;
      this.detailForm.feeAmount = 0;
      this.cdr.detectChanges();
      return;
    }

    this.detailForm.vatAmount = (amount * 7) / 107;
    this.cdr.detectChanges(); // บังคับรีเฟรช UI ทันที

    //ดึงค่าอัตราเงินคืน
    this.taxesService.calculateTaxRate(amount).subscribe({
      next: (rateData: TaxRateResponse) => {
        if (rateData) {
          const rateDataAny = rateData as any;
          const vrtrate = Number(rateDataAny.vrtrate ?? rateDataAny.vrtRate ?? 0);
          const vrtrateag = Number(rateDataAny.vrtrateag ?? rateDataAny.vrtRateAg ?? 0);

          this.detailForm.refundRevenue = vrtrate;
          this.detailForm.refundAgent = vrtrateag;
          this.detailForm.feeAmount = vrtrate - vrtrateag;
        } else {
          this.detailForm.refundRevenue = 0;
          this.detailForm.refundAgent = 0;
          this.detailForm.feeAmount = 0;
        }
        this.cdr.detectChanges(); // รี UI
      },
      error: () => {
        this.cdr.detectChanges();
      },
    });
  }
  // AddกับEdit
  addOrUpdateDetail(): void {
    if (
      !this.detailForm.bookNo ||
      !this.detailForm.docNo ||
      !this.detailForm.docDate ||
      !this.detailForm.purchaseAmount
    ) {
      alert('กรุณากรอกข้อมูล เล่มที่, เลขที่, วันที่เอกสาร และ ยอดซื้อ ให้ครบถ้วน');
      return;
    }

    if (!this.detailForm.bookNo || !this.detailForm.docNo || !this.detailForm.purchaseAmount) {
      return;
    }

    if (this.detailForm.taxId && this.detailForm.taxId.length < 13) {
      return;
    }

    const detailItem = { ...this.detailForm };

    if (this.editingIndex >= 0) {
      this.detailList[this.editingIndex] = detailItem;
      this.editingIndex = -1;
    } else {
      this.detailList.push(detailItem);
    }

    this.resetDetailForm();
    this.calculateTotals();
    this.cdr.detectChanges();
  }

  // ดึงข้อมูลมาแก้ไข
  editDetail(index: number): void {
    this.editingIndex = index;
    this.detailForm = { ...this.detailList[index] };
    this.cdr.detectChanges();
  }

  // ยกเลิกedit
  cancelEdit(): void {
    this.editingIndex = -1;
    this.resetDetailForm();
    this.cdr.detectChanges();
  }

  // ลบ
  removeDetail(index: number): void {
    const isConfirmed = confirm('คุณต้องการลบรายการนี้ออกจากตารางใช่หรือไม่?');

    this.detailList.splice(index, 1);
    if (this.editingIndex === index) {
      this.cancelEdit();
    }
    this.calculateTotals();
    this.cdr.detectChanges();
  }

  // คำนวณยอดรวมทำงานที่ADD,edit,search
  calculateTotals(): void {
    this.totalPurchase = this.detailList.reduce(
      (sum, item) => sum + (Number(item.purchaseAmount) || 0),
      0,
    );
    this.totalVat = this.detailList.reduce((sum, item) => sum + (Number(item.vatAmount) || 0), 0);
    this.totalRefund = this.detailList.reduce(
      (sum, item) => sum + (Number(item.refundRevenue) || 0),
      0,
    );
    this.totalFee = this.detailList.reduce((sum, item) => sum + (Number(item.feeAmount) || 0), 0);
  }

  // บันทึกลง Database
  saveAll(): void {
    if (this.detailList.length === 0) {
      alert('กรุณากรอกข้อมูลรายการย่อยอย่างน้อย 1 รายการก่อนบันทึก');
      return;
    }

    const payload: TaxesHeader = {
      summaryNo: this.currentSummaryNo || undefined,
      details: this.detailList,
      totalPurchase: this.totalPurchase,
      totalVat: this.totalVat,
      totalRefund: this.totalRefund,
      totalFee: this.totalFee,
    };

    this.taxesService.saveTaxes(payload).subscribe({
      next: (res: TaxesHeader) => {
        if (res && res.summaryNo) {
          this.currentSummaryNo = res.summaryNo;
          this.searchSummaryNo = res.summaryNo;
        }
        alert('บันทึกข้อมูลใบสรุปสำเร็จเรียบร้อยแล้ว!');
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Save error:', err);
        alert('เกิดข้อผิดพลาดในการบันทึกข้อมูล กรุณาลองใหม่อีกครั้ง');
        this.cdr.detectChanges();
      },
    });
  }

  // เอาไว้ search DB
  searchFromDB(): void {
    const summaryNo = this.searchSummaryNo ? this.searchSummaryNo.trim() : '';
    const summaryDate = this.searchSummaryDate || '';

    if (!summaryNo && !summaryDate) {
      return;
    }

    this.taxesService.getAllHeaders().subscribe({
      next: (headers: TaxesHeader[]) => {
        const matchedHeaders = headers.filter((h) => {
          const matchNo = summaryNo ? h.summaryNo === summaryNo : true;
          const matchDate = summaryDate ? h.summaryDate === summaryDate : true;
          return matchNo && matchDate;
        });

        if (matchedHeaders.length === 1) {
          this.confirmSelectSummary(matchedHeaders[0]);
        } else if (matchedHeaders.length > 1) {
          this.matchedSummaryList = matchedHeaders;
          this.showSelectSummaryModal = true;
        }
        //ตรวจจับUI ใหม่เพราะถ้าเข้าอันนี้ ก็เท่ากับว่าต้องเปิด modal มันจะอัพเดททันที
        this.cdr.detectChanges();
      },
      //ไว้สำหรับเช็ค error เฉยๆ
      error: (err: any) => {
        console.error('Search error:', err);
        this.cdr.detectChanges();
      },
    });
  }

  // ยืนยันการเลือกใบสรุปจาก Pop-up Modal
  confirmSelectSummary(selectedHeader: TaxesHeader): void {
    this.currentSummaryNo = selectedHeader.summaryNo || '';
    this.searchSummaryNo = selectedHeader.summaryNo || '';
    this.searchSummaryDate = selectedHeader.summaryDate || '';

    this.detailList = [...(selectedHeader.details || [])];
    this.editingIndex = -1;
    this.calculateTotals();

    this.showSelectSummaryModal = false;
    this.cdr.detectChanges();
  }

  // ล้างหน้าจอเคลียร์เฉยๆอะ
  clearForm(): void {
    this.searchSummaryNo = '';
    this.searchSummaryDate = '';
    this.currentSummaryNo = '';
    this.detailList = [];
    this.editingIndex = -1;
    this.resetDetailForm();
    this.calculateTotals();
    this.cdr.detectChanges();
  }

  // บันทึกPDF
  printReport(): void {
    if (!this.currentSummaryNo) {
      return;
    }

    this.taxesService.exportPdfReport(this.currentSummaryNo).subscribe({
      next: (blob: Blob) => {
        const pdfBlob = new Blob([blob], { type: 'application/pdf' });
        const fileURL = URL.createObjectURL(pdfBlob);

        const link = document.createElement('a');
        link.href = fileURL;
        link.download = `ภพ10_${this.currentSummaryNo}.pdf`;

        document.body.appendChild(link);
        link.click();

        document.body.removeChild(link);
        setTimeout(() => URL.revokeObjectURL(fileURL), 100);
      },
      error: (err: any) => {
        console.error('Print error:', err);
      },
    });
  }

  //functionสำหรับแสดงเป็นวันพศ
  formatThaiDate(dateStr: string | null | undefined): string {
    if (!dateStr) return '-';

    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const yearBE = date.getFullYear() + 543;

    return `${day}/${month}/${yearBE}`;
  }
}
