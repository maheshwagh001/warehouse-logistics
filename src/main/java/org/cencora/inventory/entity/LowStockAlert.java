package org.cencora.inventory.entity;


import jakarta.persistence.*;
import org.cencora.product.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "low_stock_alerts")
public class LowStockAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "product_id")
    private Product product;
    
    @Column(name = "threshold_quantity", nullable = false)
    private Integer thresholdQuantity;
    
    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;
    
    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public LowStockAlert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.alertDate = LocalDate.now();
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getThresholdQuantity() { return thresholdQuantity; }
    public void setThresholdQuantity(Integer thresholdQuantity) { this.thresholdQuantity = thresholdQuantity; }
    
    public Integer getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Integer currentQuantity) { this.currentQuantity = currentQuantity; }
    
    public LocalDate getAlertDate() { return alertDate; }
    public void setAlertDate(LocalDate alertDate) { this.alertDate = alertDate; }
    
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
    
    // Helper method
    public boolean isStillValid() {
        return currentQuantity <= thresholdQuantity;
    }
}