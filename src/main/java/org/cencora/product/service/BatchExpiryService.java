package org.cencora.product.service;

import org.cencora.product.entity.BatchExpiryTracking;
import org.cencora.product.service.dto.BatchExpiryDTO;

import java.util.List;

public interface BatchExpiryService {
    
    // Create
    BatchExpiryTracking registerBatch(BatchExpiryDTO batchDTO);
    
    // Read
    BatchExpiryTracking getBatchById(Long batchId);
    BatchExpiryTracking getBatchByNumber(String batchNumber);
    List<BatchExpiryTracking> getAllBatches();
    List<BatchExpiryTracking> getBatchesByProductId(Long productId);
    List<BatchExpiryTracking> getExpiringSoon(int daysThreshold);
    List<BatchExpiryTracking> getExpiredBatches();
    List<BatchExpiryTracking> getBatchesByStatus(String status);
    List<BatchExpiryTracking> getBatchesByLocation(Integer zoneId, Integer palletId);
    List<BatchExpiryTracking> getBatchHistory(String batchNumber);
    
    // Update
    BatchExpiryTracking updateBatch(Long batchId, BatchExpiryDTO batchDTO);
    void updateBatchStatuses();
    BatchExpiryTracking adjustQuantity(Long batchId, Integer quantityChange);
    void markAsExpired(Long batchId);
    void quarantineBatch(Long batchId);
    
    // Delete
    void deleteBatch(Long batchId);
}