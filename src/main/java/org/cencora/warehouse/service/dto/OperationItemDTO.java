package org.cencora.warehouse.service.dto;


import org.cencora.NotNull;
import org.cencora.warehouse.entity.OperationItem.ItemStatus;

public class OperationItemDTO {
    private Long itemId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String batchNumber;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    private Long sourcePalletId;
    private Long destinationPalletId;
    private ItemStatus status = ItemStatus.PENDING;
    private String notes;
    
    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Long getSourcePalletId() { return sourcePalletId; }
    public void setSourcePalletId(Long sourcePalletId) { this.sourcePalletId = sourcePalletId; }
    
    public Long getDestinationPalletId() { return destinationPalletId; }
    public void setDestinationPalletId(Long destinationPalletId) { this.destinationPalletId = destinationPalletId; }
    
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}