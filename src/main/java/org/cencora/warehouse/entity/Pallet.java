package org.cencora.warehouse.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pallets")
public class Pallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pallet_id")
    private Long palletId;
    
    @Column(name = "zone_id", nullable = false)
    private Long zoneId;
    
    @Column(name = "pallet_code", unique = true, nullable = false)
    private String palletCode;  // e.g., "PALLET-A1-001"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PalletStatus status;
    
    @Column(name = "capacity_units", nullable = false)
    private Integer capacityUnits = 100;
    
    @Column(name = "current_units", nullable = false)
    private Integer currentUnits = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // ENUM for pallet status
    public enum PalletStatus {
        AVAILABLE,    // Empty and ready
        OCCUPIED,     // Has products
        RESERVED,     // Reserved for operation
        DAMAGED,      // Not usable
        QUARANTINED   // Under inspection
    }
    
    // Constructor
    public Pallet() {
        this.createdAt = LocalDateTime.now();
        this.status = PalletStatus.AVAILABLE;
    }
    
    // Getters and Setters
    public Long getPalletId() { return palletId; }
    public void setPalletId(Long palletId) { this.palletId = palletId; }
    
    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    
    public String getPalletCode() { return palletCode; }
    public void setPalletCode(String palletCode) { this.palletCode = palletCode; }
    
    public PalletStatus getStatus() { return status; }
    public void setStatus(PalletStatus status) { this.status = status; }
    
    public Integer getCapacityUnits() { return capacityUnits; }
    public void setCapacityUnits(Integer capacityUnits) { this.capacityUnits = capacityUnits; }
    
    public Integer getCurrentUnits() { return currentUnits; }
    public void setCurrentUnits(Integer currentUnits) { this.currentUnits = currentUnits; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public Integer getAvailableSpace() {
        return capacityUnits - currentUnits;
    }
    
    public boolean hasSpaceFor(int units) {
        return getAvailableSpace() >= units;
    }
    
    public Float getFillPercentage() {
        return (currentUnits.floatValue() / capacityUnits.floatValue()) * 100;
    }
}