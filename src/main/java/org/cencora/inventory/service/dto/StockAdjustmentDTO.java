package org.cencora.inventory.service.dto;


import org.cencora.NotNull;

public class StockAdjustmentDTO {
    
    @NotNull(message = "Stock ID is required")
    private Long stockId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    @NotNull(message = "Adjustment type is required")
    private String adjustmentType; // ADD, REMOVE, RESERVE, RELEASE, DAMAGE, EXPIRED
    
    private String reason;
    private String referenceNumber; // PO, Order, etc.
    private String performedBy;
    
    // Getters and Setters
    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
}