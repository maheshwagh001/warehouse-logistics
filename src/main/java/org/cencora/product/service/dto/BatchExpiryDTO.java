package org.cencora.product.service.dto;

import org.cencora.NotNull;

import java.time.LocalDate;

public class BatchExpiryDTO {
    
    private Long batchId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Batch number is required")
    private String batchNumber;
    
    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;
    
    @NotNull(message = "Quantity is required")
    private Integer quantityCurrent;
    
    private Integer zoneId;
    private Integer palletId;
    private String status;
    
    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
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
}