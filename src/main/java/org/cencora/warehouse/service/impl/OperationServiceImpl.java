package org.cencora.warehouse.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.warehouse.entity.OperationItem;
import org.cencora.warehouse.entity.WarehouseOperation;
import org.cencora.warehouse.repository.OperationItemRepository;
import org.cencora.warehouse.repository.OperationRepository;
import org.cencora.warehouse.service.OperationService;
import org.cencora.warehouse.service.dto.OperationDTO;
import org.cencora.warehouse.service.dto.OperationItemDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OperationServiceImpl implements OperationService {
    
    @Inject
    OperationRepository operationRepository;
    
    @Inject
    OperationItemRepository operationItemRepository;
    
    @Inject
    org.cencora.inventory.service.InventoryService inventoryService;
    
    @Override
    @Transactional
    public WarehouseOperation createOperation(OperationDTO operationDTO) {
        // Check if operation number is unique
        if (operationRepository.findByOperationNumber(operationDTO.getOperationNumber()) != null) {
            throw new IllegalArgumentException("Operation number already exists: " + operationDTO.getOperationNumber());
        }
        
        WarehouseOperation operation = new WarehouseOperation();
        operation.setOperationNumber(operationDTO.getOperationNumber());
        operation.setOperationType(operationDTO.getOperationType());
        operation.setStatus(operationDTO.getStatus());
        operation.setReferenceNumber(operationDTO.getReferenceNumber());
        operation.setAssignedTo(operationDTO.getAssignedTo());
        operation.setSourceZoneId(operationDTO.getSourceZoneId());
        operation.setDestinationZoneId(operationDTO.getDestinationZoneId());
        operation.setPriority(operationDTO.getPriority());
        operation.setNotes(operationDTO.getNotes());
        operation.setCreatedBy(operationDTO.getCreatedBy());
        
        // Create operation items
        if (operationDTO.getItems() != null && !operationDTO.getItems().isEmpty()) {
            List<OperationItem> items = new ArrayList<>();
            for (OperationItemDTO itemDTO : operationDTO.getItems()) {
                OperationItem item = new OperationItem();
                item.setOperation(operation);
                item.setProductId(itemDTO.getProductId());
                item.setBatchNumber(itemDTO.getBatchNumber());
                item.setQuantity(itemDTO.getQuantity());
                item.setSourcePalletId(itemDTO.getSourcePalletId());
                item.setDestinationPalletId(itemDTO.getDestinationPalletId());
                item.setStatus(itemDTO.getStatus());
                item.setNotes(itemDTO.getNotes());
                items.add(item);
            }
            operation.setItems(items);
        }
        
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    @Transactional
    public WarehouseOperation updateOperation(Long operationId, OperationDTO operationDTO) {
        WarehouseOperation operation = getOperationById(operationId);
        
        // Update basic fields
        if (operationDTO.getAssignedTo() != null) {
            operation.setAssignedTo(operationDTO.getAssignedTo());
        }
        if (operationDTO.getPriority() != null) {
            operation.setPriority(operationDTO.getPriority());
        }
        if (operationDTO.getNotes() != null) {
            operation.setNotes(operationDTO.getNotes());
        }
        
        operation.preUpdate();
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    public WarehouseOperation getOperationById(Long operationId) {
        return operationRepository.findById(operationId);
    }
    
    @Override
    public WarehouseOperation getOperationByNumber(String operationNumber) {
        return operationRepository.findByOperationNumber(operationNumber);
    }
    
    @Override
    @Transactional
    public WarehouseOperation startOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        
        if (!operation.canStart()) {
            throw new IllegalArgumentException("Operation cannot be started. Current status: " + operation.getStatus());
        }
        
        operation.setStatus(WarehouseOperation.OperationStatus.IN_PROGRESS);
        operation.setStartedAt(LocalDateTime.now());
        operation.preUpdate();
        
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    @Transactional
    public WarehouseOperation completeOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        
        if (!operation.isInProgress()) {
            throw new IllegalArgumentException("Only operations in progress can be completed");
        }
        
        // Process operation based on type
        switch (operation.getOperationType()) {
            case INBOUND:
                processInboundOperation(operationId);
                break;
            case PICKING:
                processPickingOperation(operationId);
                break;
            case RETURN:
                processReturnOperation(operationId);
                break;
            default:
                // For other types, just mark as completed
                break;
        }
        
        operation.setStatus(WarehouseOperation.OperationStatus.COMPLETED);
        operation.setCompletedAt(LocalDateTime.now());
        operation.preUpdate();
        
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    @Transactional
    public WarehouseOperation cancelOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        operation.setStatus(WarehouseOperation.OperationStatus.CANCELLED);
        operation.preUpdate();
        
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    @Transactional
    public WarehouseOperation holdOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        operation.setStatus(WarehouseOperation.OperationStatus.ON_HOLD);
        operation.preUpdate();
        
        operationRepository.persist(operation);
        return operation;
    }
    
    @Override
    @Transactional
    public WarehouseOperation createInboundOperation(String poNumber, List<OperationItemDTO> items) {
        String operationNumber = "INB-" + System.currentTimeMillis();
        
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperationNumber(operationNumber);
        operationDTO.setOperationType(WarehouseOperation.OperationType.INBOUND);
        operationDTO.setReferenceNumber(poNumber);
        operationDTO.setStatus(WarehouseOperation.OperationStatus.PENDING);
        operationDTO.setItems(items);
        
        return createOperation(operationDTO);
    }
    
    @Override
    @Transactional
    public WarehouseOperation createPickingOperation(String orderNumber, List<OperationItemDTO> items) {
        String operationNumber = "PICK-" + System.currentTimeMillis();
        
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperationNumber(operationNumber);
        operationDTO.setOperationType(WarehouseOperation.OperationType.PICKING);
        operationDTO.setReferenceNumber(orderNumber);
        operationDTO.setStatus(WarehouseOperation.OperationStatus.PENDING);
        operationDTO.setItems(items);
        
        return createOperation(operationDTO);
    }
    
    @Override
    @Transactional
    public WarehouseOperation createReturnOperation(String returnNumber, List<OperationItemDTO> items) {
        String operationNumber = "RET-" + System.currentTimeMillis();
        
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setOperationNumber(operationNumber);
        operationDTO.setOperationType(WarehouseOperation.OperationType.RETURN);
        operationDTO.setReferenceNumber(returnNumber);
        operationDTO.setStatus(WarehouseOperation.OperationStatus.PENDING);
        operationDTO.setItems(items);
        
        return createOperation(operationDTO);
    }
    
    @Override
    public List<WarehouseOperation> getOperationsByType(WarehouseOperation.OperationType type) {
        return operationRepository.findByType(type);
    }
    
    @Override
    public List<WarehouseOperation> getPendingOperations() {
        return operationRepository.findPendingOperations();
    }
    
    @Override
    public List<WarehouseOperation> getInProgressOperations() {
        return operationRepository.findInProgressOperations();
    }
    
    @Override
    public List<WarehouseOperation> getOperationsByReference(String referenceNumber) {
        return operationRepository.findOperationsByReference(referenceNumber);
    }
    
    @Override
    @Transactional
    public void processInboundOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        
        // For each item in inbound operation
        for (OperationItem item : operation.getItems()) {
            // Add stock to inventory at destination pallet
            org.cencora.inventory.service.dto.InventoryStockDTO stockDTO = 
                new org.cencora.inventory.service.dto.InventoryStockDTO();
            stockDTO.setProductId(item.getProductId());
            stockDTO.setBatchNumber(item.getBatchNumber());
            stockDTO.setQuantityAvailable(item.getQuantity());
            stockDTO.setzoneId(Math.toIntExact(operation.getDestinationZoneId()));  // zoneId
            stockDTO.setpalletId(Math.toIntExact(item.getDestinationPalletId()));     // palletId
            stockDTO.setStatus("AVAILABLE");
            
            inventoryService.addStock(stockDTO);
            
            // Update pallet occupancy
            // (This would need PalletService injection)
            item.setStatus(OperationItem.ItemStatus.PROCESSED);
        }
    }
    
    @Override
    @Transactional
    public void processPickingOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        
        // For each item in picking operation
        for (OperationItem item : operation.getItems()) {
            // Reserve stock from inventory
            inventoryService.reserveStockForOrder(item.getProductId(), item.getQuantity());
            
            // Update pallet occupancy (reduce)
            // (This would need PalletService injection)
            item.setStatus(OperationItem.ItemStatus.PROCESSED);
        }
    }
    
    @Override
    @Transactional
    public void processReturnOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        
        // For each item in return operation
        for (OperationItem item : operation.getItems()) {
            if ("RESTOCK".equals(item.getNotes())) {
                // Add back to inventory
                org.cencora.inventory.service.dto.InventoryStockDTO stockDTO =
                    new org.cencora.inventory.service.dto.InventoryStockDTO();
                stockDTO.setProductId(item.getProductId());
                stockDTO.setBatchNumber(item.getBatchNumber());
                stockDTO.setQuantityAvailable(item.getQuantity());
                stockDTO.setzoneId(Math.toIntExact(operation.getDestinationZoneId()));  // quarantine zone
                stockDTO.setpalletId(Math.toIntExact(item.getDestinationPalletId()));     // quarantine pallet
                stockDTO.setStatus("QUARANTINED");
                
                inventoryService.addStock(stockDTO);
            } else if ("SCRAP".equals(item.getNotes())) {
                // Mark as damaged in inventory
                org.cencora.inventory.service.dto.StockAdjustmentDTO adjustment = 
                    new org.cencora.inventory.service.dto.StockAdjustmentDTO();
                adjustment.setStockId(item.getItemId());  // This would need actual stock ID
                adjustment.setQuantity(item.getQuantity());
                adjustment.setAdjustmentType("DAMAGE");
                adjustment.setReason("Returned damaged item");
                
                // inventoryService.adjustStock(adjustment);
            }
            
            item.setStatus(OperationItem.ItemStatus.PROCESSED);
        }
    }
    
    @Override
    @Transactional
    public void deleteOperation(Long operationId) {
        WarehouseOperation operation = getOperationById(operationId);
        if (operation != null) {
            operationRepository.delete(operation);
        }
    }
}