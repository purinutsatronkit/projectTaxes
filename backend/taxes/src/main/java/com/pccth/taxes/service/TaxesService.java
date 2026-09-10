package com.pccth.taxes.service;

import com.pccth.taxes.entity.TaxesHeaderEntity;
import com.pccth.taxes.entity.TaxesRdbvrtrateEntity;

import java.math.BigDecimal;
import java.util.List;

public interface TaxesService {

    // 1. คำนวณอัตราภาษีจากยอดซื้อ
    TaxesRdbvrtrateEntity calculateTaxRate(BigDecimal purchaseAmount);

    // 2. สุ่มเลข turk + 6 หลักแบบไม่ซ้ำ
    String generateUniqueSummaryNo();

    // 3. บันทึกใบสรุปพร้อมรายการย่อยทั้งหมด
    TaxesHeaderEntity saveTaxesHeader(TaxesHeaderEntity headerEntity);

    // 4. ค้นหาใบสรุปตาม ID
    TaxesHeaderEntity getHeaderById(Long id);

    // 5. ค้นหาใบสรุปตามเลขที่ใบสรุป (turkXXXXXX)
    TaxesHeaderEntity getHeaderBySummaryNo(String summaryNo);

    // 6. ดึงใบสรุปทั้งหมด (ใช้แสดงใน Modal ค้นหา)
    List<TaxesHeaderEntity> getAllHeaders();
    
    byte[] exportPdfReport(String summaryNo) throws Exception;
}