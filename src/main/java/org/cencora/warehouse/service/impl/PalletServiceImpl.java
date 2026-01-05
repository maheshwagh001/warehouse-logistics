package org.cencora.warehouse.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.warehouse.entity.Pallet;
import org.cencora.warehouse.repository.PalletRepository;
import org.cencora.warehouse.service.PalletService;
import org.cencora.warehouse.service.dto.PalletDTO;

import java.util.List;

@ApplicationScoped
public class PalletServiceImpl implements PalletService {
    
    @Inject
    PalletRepository palletRepository;
    
    @Override
    @Transactional
    public Pallet createPallet(PalletDTO palletDTO) {
        // Check if pallet code is unique
        if (palletRepository.findByPalletCode(palletDTO.getPalletCode()).isPresent()) {
            throw new IllegalArgumentException("Pallet code already exists: " + palletDTO.getPalletCode());
        }
        
        Pallet pallet = new Pallet();
        pallet.setZoneId(palletDTO.getZoneId());
        pallet.setPalletCode(palletDTO.getPalletCode());
        pallet.setStatus(palletDTO.getStatus());
        pallet.setCapacityUnits(palletDTO.getCapacityUnits());
        pallet.setCurrentUnits(palletDTO.getCurrentUnits());
        
        palletRepository.persist(pallet);
        return pallet;
    }
    
    @Override
    @Transactional
    public Pallet updatePallet(Long palletId, PalletDTO palletDTO) {
        Pallet pallet = getPalletById(palletId);
        
        // Check if new pallet code is unique
        if (!pallet.getPalletCode().equals(palletDTO.getPalletCode()) && 
            palletRepository.findByPalletCode(palletDTO.getPalletCode()).isPresent()) {
            throw new IllegalArgumentException("Pallet code already exists: " + palletDTO.getPalletCode());
        }
        
        pallet.setZoneId(palletDTO.getZoneId());
        pallet.setPalletCode(palletDTO.getPalletCode());
        pallet.setStatus(palletDTO.getStatus());
        pallet.setCapacityUnits(palletDTO.getCapacityUnits());
        pallet.setCurrentUnits(palletDTO.getCurrentUnits());
        
        palletRepository.persist(pallet);
        return pallet;
    }
    
    @Override
    public Pallet getPalletById(Long palletId) {
        return palletRepository.findById(palletId);
    }
    
    @Override
    public Pallet getPalletByCode(String palletCode) {
        return palletRepository.findByPalletCode(palletCode)
                .orElseThrow(() -> new IllegalArgumentException("Pallet not found: " + palletCode));
    }
    
    @Override
    public List<Pallet> getPalletsByZone(Long zoneId) {
        return palletRepository.findByZoneId(zoneId);
    }
    
    @Override
    public List<Pallet> getAvailablePallets() {
        return palletRepository.findAvailablePallets();
    }
    
    @Override
    public List<Pallet> getAvailablePalletsInZone(Long zoneId, int requiredUnits) {
        return palletRepository.findAvailablePalletsInZone(zoneId, requiredUnits);
    }
    
    @Override
    @Transactional
    public void updatePalletStatus(Long palletId, Pallet.PalletStatus status) {
        Pallet pallet = getPalletById(palletId);
        pallet.setStatus(status);
        palletRepository.persist(pallet);
    }
    
    @Override
    @Transactional
    public void updatePalletOccupancy(Long palletId, int unitsToAdd) {
        Pallet pallet = getPalletById(palletId);
        
        int newUnits = pallet.getCurrentUnits() + unitsToAdd;
        if (newUnits < 0 || newUnits > pallet.getCapacityUnits()) {
            throw new IllegalArgumentException("Invalid units to add. Current: " + 
                                              pallet.getCurrentUnits() + ", Add: " + unitsToAdd + 
                                              ", Capacity: " + pallet.getCapacityUnits());
        }
        
        pallet.setCurrentUnits(newUnits);
        
        // Update status based on occupancy
        if (newUnits == 0) {
            pallet.setStatus(Pallet.PalletStatus.AVAILABLE);
        } else if (newUnits > 0) {
            pallet.setStatus(Pallet.PalletStatus.OCCUPIED);
        }
        
        palletRepository.persist(pallet);
    }
    
    @Override
    @Transactional
    public void deletePallet(Long palletId) {
        Pallet pallet = getPalletById(palletId);
        if (pallet != null) {
            palletRepository.delete(pallet);
        }
    }
}