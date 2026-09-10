package com.pccth.taxes.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rdbvrtrate", schema = "taxes")
public class TaxesRdbvrtrateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SALEFROM", nullable = false, precision = 15, scale = 2)
    private BigDecimal saleFrom;

    @Column(name = "SALETO", nullable = false, precision = 15, scale = 2)
    private BigDecimal saleTo;

    @Column(name = "VRTRATE", nullable = false, precision = 10, scale = 2)
    private BigDecimal vrtRate;

    @Column(name = "VRTRATEAG", nullable = false, precision = 10, scale = 2)
    private BigDecimal vrtRateAg;

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

    public BigDecimal getSaleFrom() { return saleFrom; }
    public void setSaleFrom(BigDecimal saleFrom) { this.saleFrom = saleFrom; }

    public BigDecimal getSaleTo() { return saleTo; }
    public void setSaleTo(BigDecimal saleTo) { this.saleTo = saleTo; }

    public BigDecimal getVrtRate() { return vrtRate; }
    public void setVrtRate(BigDecimal vrtRate) { this.vrtRate = vrtRate; }

    public BigDecimal getVrtRateAg() { return vrtRateAg; }
    public void setVrtRateAg(BigDecimal vrtRateAg) { this.vrtRateAg = vrtRateAg; }

    public LocalDateTime getCreateDate() { return createDate; }
    public LocalDateTime getUpdateDate() { return updateDate; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}