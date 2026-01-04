package org.cencora.product.entity;


import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_expiry_tracking")
public class BatchExpiryTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "product_id")
    private Product product;
    
    @Column(name = "batch_number", nullable = false)
    private String batchNumber;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    @Column(name = "quantity_current", nullable = false)
    private Integer quantityCurrent;

    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "pallet_id")
    private Integer palletId;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public BatchExpiryTracking() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = "ACTIVE";
        this.quantityCurrent = 0;
    }
    
    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public Integer getQuantityCurrent() { return quantityCurrent; }
    public void setQuantityCurrent(Integer quantityCurrent) { this.quantityCurrent = quantityCurrent; }

    public Integer getzoneId() { return zoneId; }
    public void setzoneId(Integer zoneId) { this.zoneId = zoneId; }

    public Integer getpalletId() { return palletId; }
    public void setpalletId(Integer palletId) { this.palletId = palletId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
    
    public boolean isExpiringSoon(int days) {
        if (expiryDate == null) return false;
        LocalDate thresholdDate = LocalDate.now().plusDays(days);
        return !expiryDate.isBefore(LocalDate.now()) && !expiryDate.isAfter(thresholdDate);
    }
}