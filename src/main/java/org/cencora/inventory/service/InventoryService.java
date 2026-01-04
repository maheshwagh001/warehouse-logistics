package org.cencora.inventory.service;


import org.cencora.inventory.entity.InventoryStock;
import org.cencora.inventory.service.dto.InventoryStockDTO;
import org.cencora.inventory.service.dto.StockAdjustmentDTO;

import java.util.List;

public interface InventoryService {
    
    // Create
    InventoryStock addStock(InventoryStockDTO stockDTO);
    
    // Read
    InventoryStock getStockById(Long stockId);
    List<InventoryStock> getAllStock();
    List<InventoryStock> getStockByProductId(Long productId);
    List<InventoryStock> getStockByLocation(Integer zoneId, Integer palletId);
    List<InventoryStock> getStockByStatus(String status);
    Integer getAvailableQuantity(Long productId);
    List<InventoryStock> getLowStockItems(Integer threshold);
    List<InventoryStock> getExpiringSoon(int daysThreshold);
    InventoryStock getStockByProductAndBatch(Long productId, String batchNumber);
    InventoryStock getInventorySnapshot(Long productId);
    
    // Update
    InventoryStock updateStock(Long stockId, InventoryStockDTO stockDTO);
    InventoryStock adjustStock(StockAdjustmentDTO adjustment);
    InventoryStock transferStock(Long stockId, Integer newzoneId, Integer newpalletId);
    InventoryStock reserveStockForOrder(Long productId, Integer quantity);
    
    // Delete
    void deleteStock(Long stockId);


}