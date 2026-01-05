package org.cencora.warehouse.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.warehouse.service.LocationService;
import org.cencora.warehouse.repository.PalletRepository;
import org.cencora.warehouse.repository.ZoneRepository;
import org.cencora.warehouse.entity.Pallet;
import org.cencora.warehouse.entity.Zone;
import org.cencora.product.service.ProductService;
import org.cencora.product.entity.Product;
import java.util.List;

@ApplicationScoped
public class LocationServiceImpl implements LocationService {
    
    @Inject
    PalletRepository palletRepository;
    
    @Inject
    ZoneRepository zoneRepository;
    
    @Inject
    ProductService productService;
    
    @Override
    public Pallet findAvailablePalletForProduct(Long productId, Integer quantity) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        
        // Get product's required temperature zone
        String productStorageZone = product.getStorageZone();
        Zone.TemperatureType requiredTempType = mapStorageZoneToTemperatureType(productStorageZone);
        
        // Find zones with required temperature type
        List<Zone> compatibleZones = zoneRepository.findByTemperatureType(requiredTempType);
        
        // Find available pallet in compatible zones
        for (Zone zone : compatibleZones) {
            List<Pallet> availablePallets = palletRepository
                .findAvailablePalletsInZone(zone.getZoneId(), quantity);
            
            if (!availablePallets.isEmpty()) {
                // Return first available pallet
                return availablePallets.get(0);
            }
        }
        
        throw new IllegalArgumentException("No available pallet found for product: " + productId);
    }
    
    @Override
    public List<Pallet> findAvailablePallets(String temperatureType, Integer minSpace) {
        Zone.TemperatureType tempType = Zone.TemperatureType.valueOf(temperatureType);
        List<Zone> zones = zoneRepository.findByTemperatureType(tempType);
        
        List<Pallet> allAvailablePallets = new java.util.ArrayList<>();
        for (Zone zone : zones) {
            List<Pallet> pallets = palletRepository
                .findAvailablePalletsInZone(zone.getZoneId(), minSpace != null ? minSpace : 1);
            allAvailablePallets.addAll(pallets);
        }
        
        return allAvailablePallets;
    }
    
    @Override
    public boolean isProductCompatibleWithZone(Long productId, Long zoneId) {
        Product product = productService.getProductById(productId);
        Zone zone = zoneRepository.findById(zoneId);
        
        if (product == null || zone == null) {
            return false;
        }
        
        String productStorageZone = product.getStorageZone();
        Zone.TemperatureType productTempType = mapStorageZoneToTemperatureType(productStorageZone);
        
        return zone.getTemperatureType() == productTempType;
    }
    
    @Override
    public Integer getAvailableSpaceInZone(Long zoneId) {
        List<Pallet> pallets = palletRepository.findByZoneId(zoneId);
        return pallets.stream()
                .mapToInt(Pallet::getAvailableSpace)
                .sum();
    }
    
    @Override
    public Integer getTotalOccupiedSpaceInZone(Long zoneId) {
        List<Pallet> pallets = palletRepository.findByZoneId(zoneId);
        return pallets.stream()
                .mapToInt(Pallet::getCurrentUnits)
                .sum();
    }
    
    @Override
    @Transactional
    public void reservePallets(List<Long> palletIds) {
        for (Long palletId : palletIds) {
            Pallet pallet = palletRepository.findById(palletId);
            if (pallet != null && pallet.getStatus() == Pallet.PalletStatus.AVAILABLE) {
                pallet.setStatus(Pallet.PalletStatus.RESERVED);
                palletRepository.persist(pallet);
            }
        }
    }
    
    @Override
    @Transactional
    public void releasePallets(List<Long> palletIds) {
        for (Long palletId : palletIds) {
            Pallet pallet = palletRepository.findById(palletId);
            if (pallet != null && pallet.getStatus() == Pallet.PalletStatus.RESERVED) {
                pallet.setStatus(Pallet.PalletStatus.AVAILABLE);
                palletRepository.persist(pallet);
            }
        }
    }
    
    private Zone.TemperatureType mapStorageZoneToTemperatureType(String storageZone) {
        if (storageZone == null) {
            return Zone.TemperatureType.AMBIENT;
        }
        
        switch (storageZone.toUpperCase()) {
            case "COLD":
                return Zone.TemperatureType.COLD;
            case "FROZEN":
                return Zone.TemperatureType.FROZEN;
            case "SECURE":
                return Zone.TemperatureType.SECURE;
            case "CONTROLLED":
                return Zone.TemperatureType.SECURE;
            default:
                return Zone.TemperatureType.AMBIENT;
        }
    }
}