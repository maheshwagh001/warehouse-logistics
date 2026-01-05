package org.cencora.warehouse.service.dto;


import org.cencora.NotNull;
import org.cencora.warehouse.entity.WarehouseOperation.OperationType;
import org.cencora.warehouse.entity.WarehouseOperation.OperationStatus;
import org.cencora.warehouse.entity.WarehouseOperation;

import java.util.List;

public class OperationDTO {
    private Long operationId;
    
    @NotNull(message = "Operation number is required")
    private String operationNumber;
    
    @NotNull(message = "Operation type is required")
    private WarehouseOperation.OperationType operationType;
    
    private OperationStatus status = OperationStatus.PENDING;
    private String referenceNumber;
    private String assignedTo;
    private Long sourceZoneId;
    private Long destinationZoneId;
    private String priority = "NORMAL";
    private String notes;
    private String createdBy;
    
    private List<OperationItemDTO> items;
    
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
    
    public List<OperationItemDTO> getItems() { return items; }
    public void setItems(List<OperationItemDTO> items) { this.items = items; }
}