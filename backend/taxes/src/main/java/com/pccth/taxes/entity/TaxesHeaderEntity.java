package com.pccth.taxes.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "header", schema = "taxes")
public class TaxesHeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summary_no", nullable = false, unique = true, length = 20)
    private String summaryNo; // เช่น turk849201

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "total_purchase", precision = 15, scale = 2)
    private BigDecimal totalPurchase = BigDecimal.ZERO;

    @Column(name = "total_vat", precision = 15, scale = 2)
    private BigDecimal totalVat = BigDecimal.ZERO;

    @Column(name = "total_refund", precision = 15, scale = 2)
    private BigDecimal totalRefund = BigDecimal.ZERO;

    @Column(name = "total_fee", precision = 15, scale = 2)
    private BigDecimal totalFee = BigDecimal.ZERO;

    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_by", length = 100)
    private String updateBy;

    // Relationship 1-to-Many กับ TaxesDetailEntity
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaxesDetailEntity> details = new ArrayList<>();

    // Helper Methods
    public void addDetail(TaxesDetailEntity detail) {
        details.add(detail);
        detail.setHeader(this);
    }

    public void removeDetail(TaxesDetailEntity detail) {
        details.remove(detail);
        detail.setHeader(null);
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        if (this.summaryDate == null) {
            this.summaryDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSummaryNo() { return summaryNo; }
    public void setSummaryNo(String summaryNo) { this.summaryNo = summaryNo; }

    public LocalDate getSummaryDate() { return summaryDate; }
    public void setSummaryDate(LocalDate summaryDate) { this.summaryDate = summaryDate; }

    public BigDecimal getTotalPurchase() { return totalPurchase; }
    public void setTotalPurchase(BigDecimal totalPurchase) { this.totalPurchase = totalPurchase; }

    public BigDecimal getTotalVat() { return totalVat; }
    public void setTotalVat(BigDecimal totalVat) { this.totalVat = totalVat; }

    public BigDecimal getTotalRefund() { return totalRefund; }
    public void setTotalRefund(BigDecimal totalRefund) { this.totalRefund = totalRefund; }

    public BigDecimal getTotalFee() { return totalFee; }
    public void setTotalFee(BigDecimal totalFee) { this.totalFee = totalFee; }

    public LocalDateTime getCreateDate() { return createDate; }
    public LocalDateTime getUpdateDate() { return updateDate; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public List<TaxesDetailEntity> getDetails() { return details; }
    public void setDetails(List<TaxesDetailEntity> details) { this.details = details; }
}