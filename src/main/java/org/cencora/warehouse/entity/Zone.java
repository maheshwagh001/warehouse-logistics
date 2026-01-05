package org.cencora.warehouse.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "zones")
public class Zone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;
    
    @Column(name = "zone_name", unique = true, nullable = false)
    private String zoneName;  // e.g., "A1", "B2", "COLD-1"
    
    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_type", nullable = false)
    private TemperatureType temperatureType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // ENUM for temperature types
    public enum TemperatureType {
        AMBIENT,    // 15-25°C
        COLD,       // 2-8°C
        FROZEN,     // -20°C or below
        SECURE      // For controlled substances
    }
    
    // Constructor
    public Zone() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getZoneId() { return zoneId; }
    public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
    
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    
    public TemperatureType getTemperatureType() { return temperatureType; }
    public void setTemperatureType(TemperatureType temperatureType) { this.temperatureType = temperatureType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}