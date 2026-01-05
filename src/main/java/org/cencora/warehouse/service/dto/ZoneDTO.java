package org.cencora.warehouse.service.dto;


import org.cencora.NotNull;
import org.cencora.warehouse.entity.Zone.TemperatureType;

public class ZoneDTO {
    private Long zoneId;
    
    @NotNull(message = "Zone name is required")
    private String zoneName;
    
    @NotNull(message = "Temperature type is required")
    private TemperatureType temperatureType;
    
    // Getters and Setters
    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    
    public TemperatureType getTemperatureType() { return temperatureType; }
    public void setTemperatureType(TemperatureType temperatureType) { this.temperatureType = temperatureType; }
}