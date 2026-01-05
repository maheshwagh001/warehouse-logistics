package org.cencora.warehouse.service;

import org.cencora.warehouse.entity.Pallet;
import org.cencora.warehouse.service.dto.PalletDTO;

import java.util.List;

public interface PalletService {
    Pallet createPallet(PalletDTO palletDTO);
    Pallet updatePallet(Long palletId, PalletDTO palletDTO);
    Pallet getPalletById(Long palletId);
    Pallet getPalletByCode(String palletCode);
    List<Pallet> getPalletsByZone(Long zoneId);
    List<Pallet> getAvailablePallets();
    List<Pallet> getAvailablePalletsInZone(Long zoneId, int requiredUnits);
    void updatePalletStatus(Long palletId, Pallet.PalletStatus status);
    void updatePalletOccupancy(Long palletId, int unitsToAdd);
    void deletePallet(Long palletId);
}