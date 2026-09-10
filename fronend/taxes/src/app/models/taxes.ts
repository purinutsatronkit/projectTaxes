export interface TaxesDetail {
  id?: number;
  bookNo: string;
  docNo: string;
  docDate: string;
  taxId: string;
  branchNo: string;
  companyName: string;
  purchaseAmount: number;
  vatAmount: number;
  refundRevenue: number;
  refundAgent: number;
  feeAmount: number;
}

export interface TaxesHeader {
  id?: number;
  summaryNo?: string;
  summaryDate?: string;
  totalPurchase: number;
  totalVat: number;
  totalRefund: number;
  totalFee: number;
  details: TaxesDetail[];
}

export interface TaxRateResponse {
  id: number;
  saleFrom: number;
  saleTo: number;
  vrtRate: number;    // อัตราคืนเงินภาษี (สรรพากร)
  vrtRateAg: number;  // อัตราค่าธรรมเนียม (ตัวแทน)
}