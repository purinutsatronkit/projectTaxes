package com.pccth.taxes.controller;

import com.pccth.taxes.entity.TaxesHeaderEntity;
import com.pccth.taxes.entity.TaxesRdbvrtrateEntity;
import com.pccth.taxes.service.TaxesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/taxes")
@CrossOrigin(origins = "*") // อนุญาตให้ Angular ยิง HTTP Request ข้าม โดเมน/Port มาได้
public class TaxesController {

    @Autowired
    private TaxesService taxesService;

    // 1. GET /api/taxes/calculate?amount=1500 -> คำนวณอัตราภาษีจากยอดซื้อ
    @GetMapping("/calculate")
    public ResponseEntity<TaxesRdbvrtrateEntity> calculateTax(@RequestParam BigDecimal amount) {
        TaxesRdbvrtrateEntity rate = taxesService.calculateTaxRate(amount);
        if (rate != null) {
            return ResponseEntity.ok(rate);
        }
        return ResponseEntity.notFound().build();
    }

    // 2. POST /api/taxes/save -> บันทึก Header + รายการ Details ทั้งหมด
    @PostMapping("/save")
    public ResponseEntity<TaxesHeaderEntity> saveTaxes(@RequestBody TaxesHeaderEntity headerEntity) {
        TaxesHeaderEntity savedData = taxesService.saveTaxesHeader(headerEntity);
        return ResponseEntity.ok(savedData);
    }

    // 3. GET /api/taxes/headers -> ดึงใบสรุปทั้งหมด (สำหรับ Modal ค้นหา)
    @GetMapping("/headers")
    public ResponseEntity<List<TaxesHeaderEntity>> getAllHeaders() {
        return ResponseEntity.ok(taxesService.getAllHeaders());
    }

    // 4. GET /api/taxes/summary/{summaryNo} -> ค้นหาตามเลขใบสรุป (เช่น turk849201)
    @GetMapping("/summary/{summaryNo}")
    public ResponseEntity<TaxesHeaderEntity> getBySummaryNo(@PathVariable String summaryNo) {
        TaxesHeaderEntity header = taxesService.getHeaderBySummaryNo(summaryNo);
        if (header != null) {
            return ResponseEntity.ok(header);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. GET /api/taxes/{id} -> ค้นหาตาม Primary Key ID
    @GetMapping("/{id}")
    public ResponseEntity<TaxesHeaderEntity> getById(@PathVariable Long id) {
        TaxesHeaderEntity header = taxesService.getHeaderById(id);
        if (header != null) {
            return ResponseEntity.ok(header);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/export-pdf/{summaryNo}")
    public ResponseEntity<byte[]> printPdf(@PathVariable String summaryNo) {
        try {
            byte[] pdfBytes = taxesService.exportPdfReport(summaryNo);

            // 📌 ตั้งชื่อไฟล์ที่ต้องการ เช่น ภพ10_turk145666.pdf
            String fileName = "ภพ10_" + summaryNo + ".pdf";

            // กำหนด Header ให้เปิดแบบ inline ( preview ) พร้อมระบุ filename
            ContentDisposition contentDisposition = ContentDisposition.inline()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(contentDisposition);
            
            // 📌 สำคัญมาก: ต้อง Expose Header เพื่อให้ Browser ยอมนำชื่อไฟล์ไปใช้ตอน Save
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}