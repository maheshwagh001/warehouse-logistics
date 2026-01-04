package org.cencora.inventory.service.dto;


import org.cencora.NotNull;

import java.time.LocalDate;

public class InventoryStockDTO {
    
    private Long stockId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String batchNumber;
    private LocalDate expiryDate;
    
    @NotNull(message = "Available quantity is required")
    private Integer quantityAvailable;
    
    private Integer quantityReserved;
    private Integer zoneId;
    private Integer palletId;
    private String status;
    
    // Getters and Setters
    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
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
}