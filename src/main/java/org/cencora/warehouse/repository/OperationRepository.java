package org.cencora.warehouse.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.warehouse.entity.WarehouseOperation;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OperationRepository implements PanacheRepository<WarehouseOperation> {
    
    public List<WarehouseOperation> findByType(WarehouseOperation.OperationType type) {
        return find("operationType = :type", 
                   Parameters.with("type", type)).list();
    }
    
    public List<WarehouseOperation> findByStatus(WarehouseOperation.OperationStatus status) {
        return find("status = :status", 
                   Parameters.with("status", status)).list();
    }
    
    public WarehouseOperation findByOperationNumber(String operationNumber) {
        return find("operationNumber = :number", 
                   Parameters.with("number", operationNumber)).firstResult();
    }
    
    public List<WarehouseOperation> findPendingOperations() {
        return find("status = 'PENDING' or status = 'ON_HOLD'").list();
    }
    
    public List<WarehouseOperation> findInProgressOperations() {
        return find("status = 'IN_PROGRESS'").list();
    }
    
    public List<WarehouseOperation> findOperationsByReference(String referenceNumber) {
        return find("referenceNumber = :ref", 
                   Parameters.with("ref", referenceNumber)).list();
    }
    
    public List<WarehouseOperation> findOperationsByDateRange(LocalDateTime start, LocalDateTime end) {
        return find("createdAt between :start and :end", 
                   Parameters.with("start", start).and("end", end)).list();
    }
    
    public List<WarehouseOperation> findOperationsByAssignedTo(String assignedTo) {
        return find("assignedTo = :person", 
                   Parameters.with("person", assignedTo)).list();
    }
}