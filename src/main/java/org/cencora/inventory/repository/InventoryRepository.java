package org.cencora.inventory.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.inventory.entity.InventoryStock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryRepository implements PanacheRepository<InventoryStock> {
    
    public List<InventoryStock> findByProductId(Long productId) {
        return find("product.productId = :productId", 
                   Parameters.with("productId", productId)).list();
    }
    
    public Optional<InventoryStock> findByProductAndBatch(Long productId, String batchNumber) {
        return find("product.productId = :productId and batchNumber = :batchNumber", 
                   Parameters.with("productId", productId).and("batchNumber", batchNumber))
                   .firstResultOptional();
    }
    
    public List<InventoryStock> findByLocation(Integer zoneId, Integer palletId) {
        return find("zoneId = :zoneId and palletId = :palletId", 
                   Parameters.with("zoneId", zoneId).and("palletId", palletId)).list();
    }
    
    public List<InventoryStock> findByStatus(String status) {
        return find("status = :status", Parameters.with("status", status)).list();
    }
    
    public List<InventoryStock> findAvailableStock() {
        return find("status = 'AVAILABLE' and quantityAvailable > 0").list();
    }
    
    public List<InventoryStock> findLowStock(Integer threshold) {
        return find("quantityAvailable <= :threshold", 
                   Parameters.with("threshold", threshold)).list();
    }
    
    public List<InventoryStock> findExpiringSoon(LocalDate date) {
        return find("expiryDate <= :date and status = 'AVAILABLE'", 
                   Parameters.with("date", date)).list();
    }
    
    public Integer getTotalAvailableByProduct(Long productId) {
        List<InventoryStock> stocks = findByProductId(productId);
        return stocks.stream()
                .filter(stock -> "AVAILABLE".equals(stock.getStatus()))
                .mapToInt(InventoryStock::getNetAvailable)
                .sum();
    }
    
    public List<InventoryStock> findByProductAndLocation(Long productId, Integer zoneId, Integer palletId) {
        return find("product.productId = :productId and zoneId = :zoneId and palletId = :palletId", 
                   Parameters.with("productId", productId)
                            .and("zoneId", zoneId)
                            .and("palletId", palletId)).list();
    }
}