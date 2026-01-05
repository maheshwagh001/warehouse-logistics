package org.cencora.warehouse.service.dto;


import org.cencora.NotNull;
import org.cencora.warehouse.entity.Pallet.PalletStatus;

public class PalletDTO {
    private Long palletId;
    
    @NotNull(message = "Zone ID is required")
    private Long zoneId;
    
    @NotNull(message = "Pallet code is required")
    private String palletCode;
    
    private PalletStatus status = PalletStatus.AVAILABLE;
    
    @NotNull(message = "Capacity units is required")
    private Integer capacityUnits = 100;
    
    private Integer currentUnits = 0;
    
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
}