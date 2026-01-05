package org.cencora.warehouse.service;

import org.cencora.warehouse.entity.WarehouseOperation;
import org.cencora.warehouse.service.dto.OperationDTO;
import org.cencora.warehouse.service.dto.OperationItemDTO;

import java.util.List;

public interface OperationService {
    // Core operations
    WarehouseOperation createOperation(OperationDTO operationDTO);
    WarehouseOperation updateOperation(Long operationId, OperationDTO operationDTO);
    WarehouseOperation getOperationById(Long operationId);
    WarehouseOperation getOperationByNumber(String operationNumber);
    
    // Operation lifecycle
    WarehouseOperation startOperation(Long operationId);
    WarehouseOperation completeOperation(Long operationId);
    WarehouseOperation cancelOperation(Long operationId);
    WarehouseOperation holdOperation(Long operationId);
    
    // Specific operation types
    WarehouseOperation createInboundOperation(String poNumber, List<OperationItemDTO> items);
    WarehouseOperation createPickingOperation(String orderNumber, List<OperationItemDTO> items);
    WarehouseOperation createReturnOperation(String returnNumber, List<OperationItemDTO> items);
    
    // Queries
    List<WarehouseOperation> getOperationsByType(WarehouseOperation.OperationType type);
    List<WarehouseOperation> getPendingOperations();
    List<WarehouseOperation> getInProgressOperations();
    List<WarehouseOperation> getOperationsByReference(String referenceNumber);
    
    // Business logic
    void processInboundOperation(Long operationId);
    void processPickingOperation(Long operationId);
    void processReturnOperation(Long operationId);
    
    // Delete
    void deleteOperation(Long operationId);
}