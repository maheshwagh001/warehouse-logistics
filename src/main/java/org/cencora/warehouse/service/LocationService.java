package org.cencora.warehouse.service;


import org.cencora.warehouse.entity.Pallet;

import java.util.List;

public interface LocationService {
    // Location finding
    Pallet findAvailablePalletForProduct(Long productId, Integer quantity);
    List<Pallet> findAvailablePallets(String temperatureType, Integer minSpace);
    
    // Zone management
    boolean isProductCompatibleWithZone(Long productId, Long zoneId);
    
    // Space management
    Integer getAvailableSpaceInZone(Long zoneId);
    Integer getTotalOccupiedSpaceInZone(Long zoneId);
    
    // Batch operations
    void reservePallets(List<Long> palletIds);
    void releasePallets(List<Long> palletIds);
}