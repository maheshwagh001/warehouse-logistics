package org.cencora.inventory.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.inventory.entity.InventoryStock;

import org.cencora.inventory.repository.InventoryRepository;
import org.cencora.inventory.service.InventoryService;
import org.cencora.inventory.service.dto.InventoryStockDTO;
import org.cencora.inventory.service.dto.StockAdjustmentDTO;
import org.cencora.product.entity.Product;
import org.cencora.product.repository.ProductRepository;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class InventoryServiceImpl implements InventoryService {
    
    @Inject
    InventoryRepository inventoryRepository;
    
    @Inject
    ProductRepository productRepository;
    
    @Override
    @Transactional
    public InventoryStock addStock(InventoryStockDTO stockDTO) {
        // Validate product exists
        Product product = productRepository.findById(stockDTO.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + stockDTO.getProductId());
        }
        
        // Validate quantities
        if (stockDTO.getQuantityAvailable() == null || stockDTO.getQuantityAvailable() < 0) {
            throw new IllegalArgumentException("Available quantity must be non-negative");
        }
        
        if (stockDTO.getQuantityReserved() == null || stockDTO.getQuantityReserved() < 0) {
            stockDTO.setQuantityReserved(0);
        }
        
        // Create inventory record
        InventoryStock stock = new InventoryStock();
        stock.setProduct(product);
        stock.setBatchNumber(stockDTO.getBatchNumber());
        stock.setExpiryDate(stockDTO.getExpiryDate());
        stock.setQuantityAvailable(stockDTO.getQuantityAvailable());
        stock.setQuantityReserved(stockDTO.getQuantityReserved());
        stock.setzoneId(stockDTO.getzoneId());
        stock.setpalletId(stockDTO.getpalletId());
        stock.setStatus(stockDTO.getStatus() != null ? stockDTO.getStatus() : "AVAILABLE");
        
        inventoryRepository.persist(stock);
        return stock;
    }
    
    @Override
    public InventoryStock getStockById(Long stockId) {
        return inventoryRepository.findById(stockId);
    }

    @Override
    public List<InventoryStock> getAllStock() {
        return inventoryRepository.listAll();
    }

    @Override
    public List<InventoryStock> getStockByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }
    
    @Override
    public Integer getAvailableQuantity(Long productId) {
        return inventoryRepository.getTotalAvailableByProduct(productId);
    }
    
    @Override
    @Transactional
    public InventoryStock adjustStock(StockAdjustmentDTO adjustment) {
        InventoryStock stock = inventoryRepository.findById(adjustment.getStockId());
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found with ID: " + adjustment.getStockId());
        }
        
        switch (adjustment.getAdjustmentType()) {
            case "ADD":
                stock.setQuantityAvailable(stock.getQuantityAvailable() + adjustment.getQuantity());
                break;
            case "REMOVE":
                int newAvailable = stock.getQuantityAvailable() - adjustment.getQuantity();
                if (newAvailable < 0) {
                    throw new IllegalArgumentException("Insufficient available quantity");
                }
                stock.setQuantityAvailable(newAvailable);
                break;
            case "RESERVE":
                if (stock.getQuantityAvailable() < adjustment.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient available quantity to reserve");
                }
                stock.setQuantityAvailable(stock.getQuantityAvailable() - adjustment.getQuantity());
                stock.setQuantityReserved(stock.getQuantityReserved() + adjustment.getQuantity());
                break;
            case "RELEASE":
                if (stock.getQuantityReserved() < adjustment.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient reserved quantity to release");
                }
                stock.setQuantityReserved(stock.getQuantityReserved() - adjustment.getQuantity());
                stock.setQuantityAvailable(stock.getQuantityAvailable() + adjustment.getQuantity());
                break;
            case "DAMAGE":
                if (stock.getQuantityAvailable() < adjustment.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient available quantity to mark as damaged");
                }
                stock.setQuantityAvailable(stock.getQuantityAvailable() - adjustment.getQuantity());
                stock.setStatus("DAMAGED");
                break;
            case "EXPIRED":
                if (stock.getQuantityAvailable() < adjustment.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient available quantity to mark as expired");
                }
                stock.setQuantityAvailable(stock.getQuantityAvailable() - adjustment.getQuantity());
                stock.setStatus("EXPIRED");
                break;
            default:
                throw new IllegalArgumentException("Invalid adjustment type: " + adjustment.getAdjustmentType());
        }
        
        stock.preUpdate();
        inventoryRepository.persist(stock);
        return stock;
    }
    
    @Override
    @Transactional
    public InventoryStock transferStock(Long stockId, Integer newzoneId, Integer newpalletId) {
        InventoryStock stock = inventoryRepository.findById(stockId);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found with ID: " + stockId);
        }
        
        stock.setzoneId(newzoneId);
        stock.setpalletId(newpalletId);
        stock.preUpdate();
        
        inventoryRepository.persist(stock);
        return stock;
    }
    
    @Override
    public List<InventoryStock> getLowStockItems(Integer threshold) {
        return inventoryRepository.findLowStock(threshold);
    }
    
    @Override
    public List<InventoryStock> getExpiringSoon(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        return inventoryRepository.findExpiringSoon(thresholdDate);
    }
    
    @Override
    public List<InventoryStock> getStockByLocation(Integer zoneId, Integer palletId) {
        return inventoryRepository.findByLocation(zoneId, palletId);
    }

    @Override
    public List<InventoryStock> getStockByStatus(String status) {
        return inventoryRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public InventoryStock reserveStockForOrder(Long productId, Integer quantity) {
        List<InventoryStock> availableStocks = inventoryRepository
                .findByProductId(productId)
                .stream()
                .filter(InventoryStock::isAvailable)
                .sorted((s1, s2) -> {
                    // FEFO: First Expiry First Out
                    if (s1.getExpiryDate() == null && s2.getExpiryDate() == null) return 0;
                    if (s1.getExpiryDate() == null) return 1;
                    if (s2.getExpiryDate() == null) return -1;
                    return s1.getExpiryDate().compareTo(s2.getExpiryDate());
                })
                .collect(java.util.stream.Collectors.toList());
        
        int remainingQuantity = quantity;
        
        for (InventoryStock stock : availableStocks) {
            int availableQty = stock.getNetAvailable();
            int toReserve = Math.min(availableQty, remainingQuantity);
            
            if (toReserve > 0) {
                StockAdjustmentDTO adjustment = new StockAdjustmentDTO();
                adjustment.setStockId(stock.getStockId());
                adjustment.setQuantity(toReserve);
                adjustment.setAdjustmentType("RESERVE");
                adjustment.setReason("Order allocation");
                
                adjustStock(adjustment);
                remainingQuantity -= toReserve;
                
                if (remainingQuantity <= 0) {
                    return stock; // Return the first stock that was reserved
                }
            }
        }
        
        if (remainingQuantity > 0) {
            throw new IllegalArgumentException("Insufficient stock available. Missing: " + remainingQuantity);
        }
        
        return null;
    }


    @Override
    public InventoryStock getStockByProductAndBatch(Long productId, String batchNumber) {
        return inventoryRepository.findByProductAndBatch(productId, batchNumber)
                .orElse(null);
    }

    @Override
    public InventoryStock getInventorySnapshot(Long productId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteStock(Long stockId) {
        InventoryStock stock = getStockById(stockId);
        if (stock != null) {
            inventoryRepository.delete(stock);
        }
    }



//    -------------------------------------------


    @Override
    @Transactional
    public InventoryStock updateStock(Long stockId, InventoryStockDTO stockDTO) {
        InventoryStock stock = getStockById(stockId);
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found with ID: " + stockId);
        }

        // Update fields from DTO
        if (stockDTO.getBatchNumber() != null) {
            stock.setBatchNumber(stockDTO.getBatchNumber());
        }
        if (stockDTO.getExpiryDate() != null) {
            stock.setExpiryDate(stockDTO.getExpiryDate());
        }
        if (stockDTO.getQuantityAvailable() != null) {
            if (stockDTO.getQuantityAvailable() < 0) {
                throw new IllegalArgumentException("Available quantity cannot be negative");
            }
            stock.setQuantityAvailable(stockDTO.getQuantityAvailable());
        }
        if (stockDTO.getQuantityReserved() != null) {
            if (stockDTO.getQuantityReserved() < 0) {
                throw new IllegalArgumentException("Reserved quantity cannot be negative");
            }
            stock.setQuantityReserved(stockDTO.getQuantityReserved());
        }
        if (stockDTO.getzoneId() != null) {
            stock.setzoneId(stockDTO.getzoneId());
        }
        if (stockDTO.getpalletId() != null) {
            stock.setpalletId(stockDTO.getpalletId());
        }
        if (stockDTO.getStatus() != null) {
            stock.setStatus(stockDTO.getStatus());
        }

        stock.preUpdate();
        inventoryRepository.persist(stock);
        return stock;
    }


}