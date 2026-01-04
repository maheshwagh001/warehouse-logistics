package org.cencora.product.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.product.entity.BatchExpiryTracking;
import org.cencora.product.entity.Product;
import org.cencora.product.repository.BatchExpiryRepository;
import org.cencora.product.repository.ProductRepository;
import org.cencora.product.service.BatchExpiryService;
import org.cencora.product.service.dto.BatchExpiryDTO;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class BatchExpiryServiceImpl implements BatchExpiryService {

    @Inject
    BatchExpiryRepository batchRepository;

    @Inject
    ProductRepository productRepository;

    @Override
    @Transactional
    public BatchExpiryTracking registerBatch(BatchExpiryDTO batchDTO) {
        // Validate product exists
        Product product = productRepository.findById(batchDTO.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + batchDTO.getProductId());
        }

        // Check batch number uniqueness
        if (batchRepository.existsByBatchNumber(batchDTO.getBatchNumber())) {
            throw new IllegalArgumentException("Batch number already exists: " + batchDTO.getBatchNumber());
        }

        // Validate expiry date
        if (batchDTO.getExpiryDate() == null) {
            throw new IllegalArgumentException("Expiry date is required");
        }

        // Validate expiry date is not in the past for new batches
        if (batchDTO.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past");
        }

        // Validate quantity
        if (batchDTO.getQuantityCurrent() == null || batchDTO.getQuantityCurrent() < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        // Create batch record
        BatchExpiryTracking batch = new BatchExpiryTracking();
        batch.setProduct(product);
        batch.setBatchNumber(batchDTO.getBatchNumber());
        batch.setExpiryDate(batchDTO.getExpiryDate());
        batch.setQuantityCurrent(batchDTO.getQuantityCurrent());
        batch.setzoneId(batchDTO.getzoneId());
        batch.setpalletId(batchDTO.getpalletId());

        // Set status based on expiry date
        updateBatchStatus(batch);

        batchRepository.persist(batch);
        return batch;
    }
    
    @Override
    public BatchExpiryTracking getBatchById(Long batchId) {
        return batchRepository.findById(batchId);
    }
    
    @Override
    public BatchExpiryTracking getBatchByNumber(String batchNumber) {
        return batchRepository.findByBatchNumber(batchNumber)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchNumber));
    }

    @Override
    public List<BatchExpiryTracking> getAllBatches() {
        return List.of();
    }

    @Override
    public List<BatchExpiryTracking> getBatchesByProductId(Long productId) {
        return batchRepository.findByProductId(productId);
    }
    
    @Override
    public List<BatchExpiryTracking> getExpiringSoon(int daysThreshold) {
        return batchRepository.findExpiringSoon(daysThreshold);
    }
    
    @Override
    public List<BatchExpiryTracking> getExpiredBatches() {
        return batchRepository.findExpired();
    }

    @Override
    public List<BatchExpiryTracking> getBatchesByStatus(String status) {
        return List.of();
    }

    @Override
    @Transactional
    public BatchExpiryTracking updateBatch(Long batchId, BatchExpiryDTO batchDTO) {
        BatchExpiryTracking batch = getBatchById(batchId);
        if (batch == null) {
            throw new IllegalArgumentException("Batch not found with ID: " + batchId);
        }
        
        // Update product if changed
        if (!batch.getProduct().getProductId().equals(batchDTO.getProductId())) {
            Product product = productRepository.findById(batchDTO.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found with ID: " + batchDTO.getProductId());
            }
            batch.setProduct(product);
        }
        
        // Update fields
        if (batchDTO.getBatchNumber() != null) {
            // Check if new batch number is unique
            if (!batch.getBatchNumber().equals(batchDTO.getBatchNumber()) && 
                batchRepository.existsByBatchNumber(batchDTO.getBatchNumber())) {
                throw new IllegalArgumentException("Batch number already exists: " + batchDTO.getBatchNumber());
            }
            batch.setBatchNumber(batchDTO.getBatchNumber());
        }
        
        if (batchDTO.getExpiryDate() != null) {
            batch.setExpiryDate(batchDTO.getExpiryDate());
        }
        
        if (batchDTO.getQuantityCurrent() != null) {
            if (batchDTO.getQuantityCurrent() < 0) {
                throw new IllegalArgumentException("Quantity must be non-negative");
            }
            batch.setQuantityCurrent(batchDTO.getQuantityCurrent());
        }
        
        if (batchDTO.getzoneId() != null) {
            batch.setzoneId(batchDTO.getzoneId());
        }
        
        if (batchDTO.getpalletId() != null) {
            batch.setpalletId(batchDTO.getpalletId());
        }
        
        if (batchDTO.getStatus() != null) {
            batch.setStatus(batchDTO.getStatus());
        } else {
            updateBatchStatus(batch);
        }
        
        batch.preUpdate();
        batchRepository.persist(batch);
        return batch;
    }
    
    @Override
    @Transactional
    public void updateBatchStatuses() {
        List<BatchExpiryTracking> allBatches = batchRepository.listAll();
        
        for (BatchExpiryTracking batch : allBatches) {
            String oldStatus = batch.getStatus();
            updateBatchStatus(batch);
            
            if (!oldStatus.equals(batch.getStatus())) {
                batch.preUpdate();
                batchRepository.persist(batch);
            }
        }
    }
    
    @Override
    @Transactional
    public BatchExpiryTracking adjustQuantity(Long batchId, Integer quantityChange) {
        if (quantityChange == 0) {
            throw new IllegalArgumentException("Quantity change cannot be zero");
        }
        
        BatchExpiryTracking batch = getBatchById(batchId);
        
        int newQuantity = batch.getQuantityCurrent() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative. Current: " + 
                                              batch.getQuantityCurrent() + ", Change: " + quantityChange);
        }
        
        batch.setQuantityCurrent(newQuantity);
        updateBatchStatus(batch);
        batch.preUpdate();
        
        batchRepository.persist(batch);
        return batch;
    }
    
    @Override
    @Transactional
    public void markAsExpired(Long batchId) {
        BatchExpiryTracking batch = getBatchById(batchId);
        batch.setStatus("EXPIRED");
        batch.preUpdate();
        batchRepository.persist(batch);
    }
    
    @Override
    @Transactional
    public void quarantineBatch(Long batchId) {
        BatchExpiryTracking batch = getBatchById(batchId);
        batch.setStatus("QUARANTINED");
        batch.preUpdate();
        batchRepository.persist(batch);
    }

    @Override
    public void deleteBatch(Long batchId) {

    }

    @Override
    public List<BatchExpiryTracking> getBatchHistory(String batchNumber) {
        return batchRepository.find("batchNumber = :batchNumber order by lastUpdated desc", 
                                  io.quarkus.panache.common.Parameters.with("batchNumber", batchNumber))
                             .list();
    }
    
    @Override
    public List<BatchExpiryTracking> getBatchesByLocation(Integer zoneId, Integer palletId) {
        return batchRepository.findByLocation(zoneId, palletId);
    }
    
    private void updateBatchStatus(BatchExpiryTracking batch) {
        if (batch.isExpired()) {
            batch.setStatus("EXPIRED");
        } else if (batch.isExpiringSoon(30)) { // 30 days threshold for expiring soon
            batch.setStatus("EXPIRING_SOON");
        } else if ("QUARANTINED".equals(batch.getStatus())) {
            // Keep quarantine status
        } else {
            batch.setStatus("ACTIVE");
        }
    }
}