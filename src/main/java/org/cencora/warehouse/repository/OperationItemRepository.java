package org.cencora.warehouse.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.warehouse.entity.OperationItem;

import java.util.List;

@ApplicationScoped
public class OperationItemRepository implements PanacheRepository<OperationItem> {
    
    public List<OperationItem> findByOperationId(Long operationId) {
        return find("operation.operationId = :opId", 
                   Parameters.with("opId", operationId)).list();
    }
    
    public List<OperationItem> findByProductId(Long productId) {
        return find("productId = :productId", 
                   Parameters.with("productId", productId)).list();
    }
    
    public List<OperationItem> findByPalletId(Long palletId) {
        return find("sourcePalletId = :palletId or destinationPalletId = :palletId", 
                   Parameters.with("palletId", palletId)).list();
    }
    
    public List<OperationItem> findByStatus(OperationItem.ItemStatus status) {
        return find("status = :status", 
                   Parameters.with("status", status)).list();
    }
}