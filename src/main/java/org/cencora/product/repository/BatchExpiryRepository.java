package org.cencora.product.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.product.entity.BatchExpiryTracking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BatchExpiryRepository implements PanacheRepository<BatchExpiryTracking> {
    
    public Optional<BatchExpiryTracking> findByBatchNumber(String batchNumber) {
        return find("batchNumber = :batchNumber", 
                   Parameters.with("batchNumber", batchNumber)).firstResultOptional();
    }
    
    public List<BatchExpiryTracking> findByProductId(Long productId) {
        return find("product.productId = :productId order by expiryDate asc", 
                   Parameters.with("productId", productId)).list();
    }
    
    public List<BatchExpiryTracking> findExpiringSoon(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        return find("expiryDate between :today and :thresholdDate and status = 'ACTIVE'", 
                   Parameters.with("today", today).and("thresholdDate", thresholdDate)).list();
    }
    
    public List<BatchExpiryTracking> findExpired() {
        return find("expiryDate < :today and status != 'EXPIRED'", 
                   Parameters.with("today", LocalDate.now())).list();
    }
    
    public List<BatchExpiryTracking> findByStatus(String status) {
        return find("status = :status", Parameters.with("status", status)).list();
    }
    
    public List<BatchExpiryTracking> findByLocation(Integer zoneId, Integer palletId) {
        return find("zoneId = :zoneId and palletId = :palletId", 
                   Parameters.with("zoneId", zoneId).and("palletId", palletId)).list();
    }
    
    public boolean existsByBatchNumber(String batchNumber) {
        return count("batchNumber = :batchNumber", 
                    Parameters.with("batchNumber", batchNumber)) > 0;
    }
    
    public List<BatchExpiryTracking> findByProductAndLocation(Long productId, Integer zoneId, Integer palletId) {
        return find("product.productId = :productId and zoneId = :zoneId and palletId = :palletId", 
                   Parameters.with("productId", productId)
                            .and("zoneId", zoneId)
                            .and("palletId", palletId)).list();
    }
}