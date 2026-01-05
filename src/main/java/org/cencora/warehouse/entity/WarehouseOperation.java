package org.cencora.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "warehouse_operations")
public class WarehouseOperation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Long operationId;
    
    @Column(name = "operation_number", unique = true, nullable = false)
    private String operationNumber;  // e.g., "INB-001", "PICK-001", "RET-001"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;
    
    @Column(name = "reference_number")  // PO Number, Order Number, Return Number
    private String referenceNumber;
    
    @Column(name = "assigned_to")  // Who is handling it
    private String assignedTo;
    
    @Column(name = "source_zone_id")
    private Long sourceZoneId;  // For picking/returns
    
    @Column(name = "destination_zone_id")
    private Long destinationZoneId;  // For inbound/putaway
    
    @Column(name = "priority")
    private String priority = "NORMAL";  // HIGH, NORMAL, LOW
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OperationItem> items;
    
    // ENUMS for operation types and status
    public enum OperationType {
        INBOUND,      // PO Receiving
        PUTAWAY,      // Stock putaway
        PICKING,      // Order picking
        PACKING,      // Order packing
        RETURN,       // Returns processing
        TRANSFER,     // Stock transfer
        CYCLE_COUNT   // Inventory count
    }
    
    public enum OperationStatus {
        PENDING,      // Just created
        IN_PROGRESS,  // Being processed
        COMPLETED,    // Finished successfully
        CANCELLED,    // Cancelled
        ON_HOLD       // Paused
    }
    
    // Constructor
    public WarehouseOperation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OperationStatus.PENDING;
    }
    
    // Getters and Setters
    public Long getOperationId() { return operationId; }
    public void setOperationId(Long operationId) { this.operationId = operationId; }
    
    public String getOperationNumber() { return operationNumber; }
    public void setOperationNumber(String operationNumber) { this.operationNumber = operationNumber; }
    
    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }
    
    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public Long getSourceZoneId() { return sourceZoneId; }
    public void setSourceZoneId(Long sourceZoneId) { this.sourceZoneId = sourceZoneId; }
    
    public Long getDestinationZoneId() { return destinationZoneId; }
    public void setDestinationZoneId(Long destinationZoneId) { this.destinationZoneId = destinationZoneId; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<OperationItem> getItems() { return items; }
    public void setItems(List<OperationItem> items) { this.items = items; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isInProgress() {
        return status == OperationStatus.IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return status == OperationStatus.COMPLETED;
    }
    
    public boolean canStart() {
        return status == OperationStatus.PENDING || status == OperationStatus.ON_HOLD;
    }
}