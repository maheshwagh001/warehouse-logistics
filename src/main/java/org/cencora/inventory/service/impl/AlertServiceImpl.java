package org.cencora.inventory.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.inventory.entity.LowStockAlert;
import org.cencora.inventory.repository.AlertRepository;
import org.cencora.inventory.service.AlertService;
import org.cencora.inventory.service.InventoryService;
import org.cencora.product.entity.Product;
import org.cencora.product.repository.ProductRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AlertServiceImpl implements AlertService {
    
    @Inject
    AlertRepository alertRepository;
    
    @Inject
    ProductRepository productRepository;
    
    @Inject
    InventoryService inventoryService;
    
    @Override
    public List<LowStockAlert> getActiveLowStockAlerts() {
        return alertRepository.findActiveAlerts();
    }
    
    @Override
    public List<LowStockAlert> getActiveAlerts() {
        return alertRepository.findByStatus("ACTIVE");
    }
    
    @Override
    public List<LowStockAlert> getResolvedAlerts() {
        return alertRepository.findByStatus("RESOLVED");
    }
    
    @Override
    public LowStockAlert getAlertById(Long id) {
        return alertRepository.findById(id);
    }
    
    @Override
    public List<LowStockAlert> getAlertsByProductId(Long productId) {
        return alertRepository.findByProductId(productId);
    }
    
    @Override
    @Transactional
    public List<LowStockAlert> checkAndGenerateAlerts() {
        List<LowStockAlert> generatedAlerts = new ArrayList<>();
        
        // Get all products with reorder points
        List<Product> products = productRepository.findProductsNeedingReorder();
        
        for (Product product : products) {
            if (product.getReorderPoint() == null) continue;
            
            // Get current available quantity
            Integer currentQuantity = inventoryService.getAvailableQuantity(product.getProductId());
            
            if (currentQuantity <= product.getReorderPoint()) {
                // Check if alert already exists
                List<LowStockAlert> existingAlerts = alertRepository.findActiveByProduct(product.getProductId());
                
                if (existingAlerts.isEmpty()) {
                    // Create new alert
                    LowStockAlert alert = new LowStockAlert();
                    alert.setProduct(product);
                    alert.setThresholdQuantity(product.getReorderPoint());
                    alert.setCurrentQuantity(currentQuantity);
                    alert.setAlertDate(LocalDate.now());
                    alert.setStatus("ACTIVE");
                    
                    alertRepository.persist(alert);
                    generatedAlerts.add(alert);
                } else {
                    // Update existing alert
                    for (LowStockAlert existingAlert : existingAlerts) {
                        existingAlert.setCurrentQuantity(currentQuantity);
                        existingAlert.setAlertDate(LocalDate.now());
                        existingAlert.preUpdate();
                        alertRepository.persist(existingAlert);
                        generatedAlerts.add(existingAlert);
                    }
                }
            }
        }
        
        return generatedAlerts;
    }
    
    @Override
    @Transactional
    public void resolveAlert(Long id) {
        LowStockAlert alert = alertRepository.findById(id);
        if (alert != null) {
            // Check if still low stock
            Integer currentQuantity = inventoryService.getAvailableQuantity(alert.getProduct().getProductId());
            
            if (currentQuantity > alert.getThresholdQuantity()) {
                alert.setStatus("RESOLVED");
                alert.setCurrentQuantity(currentQuantity);
                alert.preUpdate();
                alertRepository.persist(alert);
            }
        }
    }
    
    @Override
    @Transactional
    public void acknowledgeAlert(Long id) {
        LowStockAlert alert = alertRepository.findById(id);
        if (alert != null && "ACTIVE".equals(alert.getStatus())) {
            alert.setStatus("ACKNOWLEDGED");
            alert.preUpdate();
            alertRepository.persist(alert);
        }
    }
    
    @Override
    @Transactional
    public void deleteAlert(Long id) {
        LowStockAlert alert = alertRepository.findById(id);
        if (alert != null) {
            alertRepository.delete(alert);
        }
    }
    
    @Override
    @Transactional
    public int cleanOldResolvedAlerts(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return alertRepository.deleteOldResolvedAlerts(cutoffDate);
    }
}