package com.pccth.taxes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "detail", schema = "taxes")
public class TaxesDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship Many-to-1 กับ TaxesHeaderEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id", nullable = false)
    @JsonIgnore
    private TaxesHeaderEntity header;

    @Column(name = "book_no", nullable = false, length = 50)
    private String bookNo;

    @Column(name = "doc_no", nullable = false, length = 50)
    private String docNo;

    @Column(name = "doc_date", nullable = false)
    private LocalDate docDate;

    @Column(name = "tax_id", length = 13)
    private String taxId;

    @Column(name = "branch_no", length = 5)
    private String branchNo;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "purchase_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "vat_amount", precision = 15, scale = 2)
    private BigDecimal vatAmount = BigDecimal.ZERO;

    @Column(name = "refund_revenue", precision = 15, scale = 2)
    private BigDecimal refundRevenue = BigDecimal.ZERO;

    @Column(name = "refund_agent", precision = 15, scale = 2)
    private BigDecimal refundAgent = BigDecimal.ZERO;

    @Column(name = "fee_amount", precision = 15, scale = 2)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_by", length = 100)
    private String updateBy;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TaxesHeaderEntity getHeader() { return header; }
    public void setHeader(TaxesHeaderEntity header) { this.header = header; }

    public String getBookNo() { return bookNo; }
    public void setBookNo(String bookNo) { this.bookNo = bookNo; }

    public String getDocNo() { return docNo; }
    public void setDocNo(String docNo) { this.docNo = docNo; }

    public LocalDate getDocDate() { return docDate; }
    public void setDocDate(LocalDate docDate) { this.docDate = docDate; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getBranchNo() { return branchNo; }
    public void setBranchNo(String branchNo) { this.branchNo = branchNo; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getPurchaseAmount() { return purchaseAmount; }
    public void setPurchaseAmount(BigDecimal purchaseAmount) { this.purchaseAmount = purchaseAmount; }

    public BigDecimal getVatAmount() { return vatAmount; }
    public void setVatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; }

    public BigDecimal getRefundRevenue() { return refundRevenue; }
    public void setRefundRevenue(BigDecimal refundRevenue) { this.refundRevenue = refundRevenue; }

    public BigDecimal getRefundAgent() { return refundAgent; }
    public void setRefundAgent(BigDecimal refundAgent) { this.refundAgent = refundAgent; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public LocalDateTime getCreateDate() { return createDate; }
    public LocalDateTime getUpdateDate() { return updateDate; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}