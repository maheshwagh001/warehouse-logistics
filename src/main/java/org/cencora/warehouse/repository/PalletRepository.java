package org.cencora.warehouse.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.warehouse.entity.Pallet;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PalletRepository implements PanacheRepository<Pallet> {
    
    public List<Pallet> findByZoneId(Long zoneId) {
        return find("zoneId = :zoneId", 
                   Parameters.with("zoneId", zoneId)).list();
    }
    
    public Optional<Pallet> findByPalletCode(String palletCode) {
        return find("palletCode = :code", 
                   Parameters.with("code", palletCode)).firstResultOptional();
    }
    
    public List<Pallet> findAvailablePallets() {
        return find("status = 'AVAILABLE' or status = 'RESERVED'").list();
    }
    
    public List<Pallet> findAvailablePalletsInZone(Long zoneId, int requiredUnits) {
        return find("zoneId = :zoneId and (status = 'AVAILABLE' or status = 'RESERVED') " +
                   "and (capacityUnits - currentUnits) >= :requiredUnits",
                   Parameters.with("zoneId", zoneId)
                            .and("requiredUnits", requiredUnits)).list();
    }
    
    public List<Pallet> findOccupiedPallets() {
        return find("status = 'OCCUPIED'").list();
    }
    
    public List<Pallet> findPalletsWithAvailableSpace(int minAvailableSpace) {
        return find("(capacityUnits - currentUnits) >= :space",
                   Parameters.with("space", minAvailableSpace)).list();
    }
}