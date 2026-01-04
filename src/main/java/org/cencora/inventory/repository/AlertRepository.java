package org.cencora.inventory.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.inventory.entity.LowStockAlert;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AlertRepository implements PanacheRepository<LowStockAlert> {
    
    public List<LowStockAlert> findActiveAlerts() {
        return find("status = 'ACTIVE' order by alertDate desc").list();
    }
    
    public List<LowStockAlert> findByStatus(String status) {
        return find("status = :status", Parameters.with("status", status)).list();
    }
    
    public List<LowStockAlert> findByProductId(Long productId) {
        return find("product.productId = :productId", 
                   Parameters.with("productId", productId)).list();
    }
    
    public List<LowStockAlert> findActiveByProduct(Long productId) {
        return find("product.productId = :productId and status = 'ACTIVE'", 
                   Parameters.with("productId", productId)).list();
    }
    
    public int deleteOldResolvedAlerts(LocalDate cutoffDate) {
        return Math.toIntExact(delete("status = 'RESOLVED' and alertDate < :cutoffDate",
                Parameters.with("cutoffDate", cutoffDate)));
    }
}