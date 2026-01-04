package org.cencora.inventory.entity;

import jakarta.persistence.*;
import org.cencora.product.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_stock")
public class InventoryStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "product_id")
    private Product product;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable = 0;
    
    @Column(name = "quantity_reserved", nullable = false)
    private Integer quantityReserved = 0;

    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "pallet_id")
    private Integer palletId;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public InventoryStock() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "AVAILABLE";
    }
    
    // Getters and Setters
    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    
    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) { this.quantityReserved = quantityReserved; }

    public Integer getzoneId() { return zoneId; }
    public void setzoneId(Integer zoneId) { this.zoneId = zoneId; }

    public Integer getpalletId() { return palletId; }
    public void setpalletId(Integer palletId) { this.palletId = palletId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public Integer getNetAvailable() {
        return quantityAvailable - quantityReserved;
    }
    
    public boolean isAvailable() {
        return "AVAILABLE".equals(status) && getNetAvailable() > 0;
    }
}