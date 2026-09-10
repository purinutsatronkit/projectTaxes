package com.pccth.taxes.service;

import com.pccth.taxes.entity.TaxesDetailEntity;
import com.pccth.taxes.entity.TaxesHeaderEntity;
import com.pccth.taxes.entity.TaxesRdbvrtrateEntity;
import com.pccth.taxes.repository.TaxesHeaderRepository;
import com.pccth.taxes.repository.TaxesRdbvrtrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TaxesServiceImpl implements TaxesService {

    @Autowired
    private TaxesHeaderRepository taxesHeaderRepository;

    @Autowired
    private TaxesRdbvrtrateRepository taxesRdbvrtrateRepository;

    @Override
    public TaxesRdbvrtrateEntity calculateTaxRate(BigDecimal purchaseAmount) {
        if (purchaseAmount == null || purchaseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return taxesRdbvrtrateRepository.findRateByPurchaseAmount(purchaseAmount)
                .orElse(null);
    }

    @Override
    public String generateUniqueSummaryNo() {
        Random random = new Random();
        String summaryNo;
        do {
            int number = 100000 + random.nextInt(900000);
            summaryNo = "turk" + number;
        } while (taxesHeaderRepository.existsBySummaryNo(summaryNo));

        return summaryNo;
    }

    @Override
    @Transactional
    public TaxesHeaderEntity saveTaxesHeader(TaxesHeaderEntity headerEntity) {
        TaxesHeaderEntity entityToSave;

        if (headerEntity.getSummaryNo() != null && !headerEntity.getSummaryNo().trim().isEmpty()) {
            String cleanSummaryNo = headerEntity.getSummaryNo().trim();
            entityToSave = taxesHeaderRepository.findBySummaryNo(cleanSummaryNo)
                    .orElseGet(() -> {
                        headerEntity.setSummaryNo(cleanSummaryNo);
                        return headerEntity;
                    });
        } else {
            entityToSave = new TaxesHeaderEntity();
            entityToSave.setSummaryNo(generateUniqueSummaryNo());
        }

        if (entityToSave.getId() != null) {
            if (entityToSave.getDetails() != null) {
                entityToSave.getDetails().clear();
            } else {
                entityToSave.setDetails(new ArrayList<>());
            }
        } else {
            entityToSave.setDetails(new ArrayList<>());
        }

        BigDecimal totalPurchase = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalRefund = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;

        if (headerEntity.getDetails() != null && !headerEntity.getDetails().isEmpty()) {
            for (TaxesDetailEntity detailDto : headerEntity.getDetails()) {
                TaxesDetailEntity detail = new TaxesDetailEntity();

                detail.setBookNo(detailDto.getBookNo());
                detail.setDocNo(detailDto.getDocNo());
                detail.setDocDate(detailDto.getDocDate());
                detail.setCompanyName(detailDto.getCompanyName());
                detail.setTaxId(detailDto.getTaxId());
                detail.setBranchNo(detailDto.getBranchNo());
                detail.setPurchaseAmount(detailDto.getPurchaseAmount());
                detail.setVatAmount(detailDto.getVatAmount());
                detail.setRefundRevenue(detailDto.getRefundRevenue());
                detail.setRefundAgent(detailDto.getRefundAgent());

                detail.setHeader(entityToSave);

                if (detail.getPurchaseAmount() != null) {
                    totalPurchase = totalPurchase.add(detail.getPurchaseAmount());

                    BigDecimal vat = detail.getPurchaseAmount()
                            .multiply(new BigDecimal("7"))
                            .divide(new BigDecimal("107"), 2, RoundingMode.HALF_UP);

                    if (detail.getVatAmount() == null || detail.getVatAmount().compareTo(BigDecimal.ZERO) == 0) {
                        detail.setVatAmount(vat);
                    }
                    totalVat = totalVat.add(detail.getVatAmount());
                }

                if (detail.getRefundRevenue() != null && detail.getRefundAgent() != null) {
                    BigDecimal fee = detail.getRefundRevenue().subtract(detail.getRefundAgent());
                    detail.setFeeAmount(fee);
                }

                if (detail.getRefundRevenue() != null) {
                    totalRefund = totalRefund.add(detail.getRefundRevenue());
                }
                if (detail.getFeeAmount() != null) {
                    totalFee = totalFee.add(detail.getFeeAmount());
                }

                entityToSave.getDetails().add(detail);
            }
        }

        entityToSave.setTotalPurchase(totalPurchase);
        entityToSave.setTotalVat(totalVat);
        entityToSave.setTotalRefund(totalRefund);
        entityToSave.setTotalFee(totalFee);

        return taxesHeaderRepository.save(entityToSave);
    }

    @Override
    public TaxesHeaderEntity getHeaderById(Long id) {
        return taxesHeaderRepository.findById(id).orElse(null);
    }

    @Override
    public TaxesHeaderEntity getHeaderBySummaryNo(String summaryNo) {
        return taxesHeaderRepository.findBySummaryNo(summaryNo).orElse(null);
    }

    @Override
    public List<TaxesHeaderEntity> getAllHeaders() {
        return taxesHeaderRepository.findAll();
    }

    @Override
    public byte[] exportPdfReport(String summaryNo) throws Exception {
        TaxesHeaderEntity header = getHeaderBySummaryNo(summaryNo);
        if (header == null) {
            throw new RuntimeException("ไม่พบข้อมูลใบสรุปเลขที่: " + summaryNo);
        }

        ClassPathResource resource = new ClassPathResource("reports/taxes_summary.jrxml");
        if (!resource.exists()) {
            throw new RuntimeException("ไม่พบไฟล์รายงานใน Classpath: reports/taxes_summary.jrxml");
        }

        // โหลดและลงทะเบียนฟอนต์สำหรับ AWT
        ClassPathResource fontResource = new ClassPathResource("fonts/THSarabunPSK.ttf");
        if (fontResource.exists()) {
            try (InputStream fontStream = fontResource.getInputStream()) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
            }
        }

        System.setProperty("net.sf.jasperreports.compiler.xml.validation", "false");

        try (InputStream reportStream = resource.getInputStream()) {
            JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            // กำหนด Locale ภาษาไทย และ Formatter สำหรับแปลง ค.ศ. -> พ.ศ.
            Locale thaiLocale = Locale.forLanguageTag("th-TH");
            DateTimeFormatter thaiDateFormatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy", thaiLocale)
                    .withChronology(ThaiBuddhistChronology.INSTANCE);

            DateTimeFormatter thaiDateTimeFormatter = DateTimeFormatter
                    .ofPattern("dd MMMM yyyy HH:mm:ss", thaiLocale)
                    .withChronology(ThaiBuddhistChronology.INSTANCE);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("summaryNo", header.getSummaryNo() != null ? header.getSummaryNo() : "");

            if (header.getSummaryDate() != null) {
                ThaiBuddhistDate thaiSummaryDate = ThaiBuddhistDate.from(header.getSummaryDate());
                parameters.put("summaryDate", thaiSummaryDate.format(thaiDateFormatter));
            } else {
                parameters.put("summaryDate", "-");
            }

            // จัดฟอร์แมตวันที่พิมพ์ (ส่ง LocalDateTime.now() เข้า Formatter ตรงๆ)
            parameters.put("printDate", LocalDateTime.now().format(thaiDateTimeFormatter));

            List<TaxesDetailEntity> details = header.getDetails() != null ? header.getDetails() : new ArrayList<>();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(details);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}