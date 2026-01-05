package org.cencora.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_items")
public class OperationItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private WarehouseOperation operation;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "source_pallet_id")
    private Long sourcePalletId;  // For picking/returns
    
    @Column(name = "destination_pallet_id")
    private Long destinationPalletId;  // For inbound/putaway
    
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status")
    private ItemStatus status;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // ENUM for item status
    public enum ItemStatus {
        PENDING,
        PROCESSED,
        VERIFIED,
        REJECTED,
        QUARANTINED
    }
    
    // Constructor
    public OperationItem() {
        this.createdAt = LocalDateTime.now();
        this.status = ItemStatus.PENDING;
    }
    
    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    
    public WarehouseOperation getOperation() { return operation; }
    public void setOperation(WarehouseOperation operation) { this.operation = operation; }
    
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
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper method
    public boolean isProcessed() {
        return status == ItemStatus.PROCESSED || status == ItemStatus.VERIFIED;
    }
}