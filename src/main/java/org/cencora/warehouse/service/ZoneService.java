package org.cencora.warehouse.service;


import org.cencora.warehouse.entity.Zone;
import org.cencora.warehouse.service.dto.ZoneDTO;

import java.util.List;

public interface ZoneService {
    Zone createZone(ZoneDTO zoneDTO);
    Zone updateZone(Long zoneId, ZoneDTO zoneDTO);
    Zone getZoneById(Long zoneId);
    Zone getZoneByName(String zoneName);
    List<Zone> getAllZones();
    List<Zone> getZonesByTemperatureType(Zone.TemperatureType type);
    void deleteZone(Long zoneId);
    boolean isZoneNameUnique(String zoneName);
}