package org.cencora.inventory.service;

import org.cencora.inventory.entity.LowStockAlert;

import java.util.List;

public interface AlertService {
    
    // Read
    List<LowStockAlert> getActiveLowStockAlerts();
    List<LowStockAlert> getActiveAlerts();
    List<LowStockAlert> getResolvedAlerts();
    LowStockAlert getAlertById(Long id);
    List<LowStockAlert> getAlertsByProductId(Long productId);
    
    // Business Logic
    List<LowStockAlert> checkAndGenerateAlerts();
    void resolveAlert(Long id);
    void acknowledgeAlert(Long id);
    
    // Delete
    void deleteAlert(Long id);
    int cleanOldResolvedAlerts(int days);
}