import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaxesHeader, TaxRateResponse } from '../models/taxes';

@Injectable({
  providedIn: 'root'
})
export class TaxesService {
  private baseUrl = 'http://localhost:8080/api/taxes';

  constructor(private http: HttpClient) {}

  // คำนวณอัตราภาษีจากยอดซื้อ
  calculateTaxRate(amount: number): Observable<TaxRateResponse> {
    return this.http.get<TaxRateResponse>(`${this.baseUrl}/calculate?amount=${amount}`);
  }

  // บันทึกข้อมูล Header + Details
  saveTaxes(data: TaxesHeader): Observable<TaxesHeader> {
    return this.http.post<TaxesHeader>(`${this.baseUrl}/save`, data);
  }

  // ดึงข้อมูลใบสรุปทั้งหมด (สำหรับ Modal ค้นหา)
  getAllHeaders(): Observable<TaxesHeader[]> {
    return this.http.get<TaxesHeader[]>(`${this.baseUrl}/headers`);
  }

  // ค้นหาตามเลขใบสรุป (turkXXXXXX)
  getBySummaryNo(summaryNo: string): Observable<TaxesHeader> {
    return this.http.get<TaxesHeader>(`${this.baseUrl}/summary/${summaryNo}`);
  }
  exportPdfReport(summaryNo: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/export-pdf/${summaryNo}`, { responseType: 'blob' });
  }
}