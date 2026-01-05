package org.cencora.warehouse.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.warehouse.entity.Zone;
import org.cencora.warehouse.repository.ZoneRepository;
import org.cencora.warehouse.service.ZoneService;
import org.cencora.warehouse.service.dto.ZoneDTO;

import java.util.List;

@ApplicationScoped
public class ZoneServiceImpl implements ZoneService {
    
    @Inject
    ZoneRepository zoneRepository;
    
    @Override
    @Transactional
    public Zone createZone(ZoneDTO zoneDTO) {
        // Validate zone name uniqueness
        if (zoneRepository.existsByZoneName(zoneDTO.getZoneName())) {
            throw new IllegalArgumentException("Zone name already exists: " + zoneDTO.getZoneName());
        }
        
        Zone zone = new Zone();
        zone.setZoneName(zoneDTO.getZoneName());
        zone.setTemperatureType(zoneDTO.getTemperatureType());
        
        zoneRepository.persist(zone);
        return zone;
    }
    
    @Override
    @Transactional
    public Zone updateZone(Long zoneId, ZoneDTO zoneDTO) {
        Zone zone = getZoneById(zoneId);
        
        // Check if new zone name is unique
        if (!zone.getZoneName().equals(zoneDTO.getZoneName()) && 
            zoneRepository.existsByZoneName(zoneDTO.getZoneName())) {
            throw new IllegalArgumentException("Zone name already exists: " + zoneDTO.getZoneName());
        }
        
        zone.setZoneName(zoneDTO.getZoneName());
        zone.setTemperatureType(zoneDTO.getTemperatureType());
        
        zoneRepository.persist(zone);
        return zone;
    }
    
    @Override
    public Zone getZoneById(Long zoneId) {
        return zoneRepository.findById(zoneId);
    }
    
    @Override
    public Zone getZoneByName(String zoneName) {
        return zoneRepository.findByZoneName(zoneName);
    }
    
    @Override
    public List<Zone> getAllZones() {
        return zoneRepository.findAllActive();
    }
    
    @Override
    public List<Zone> getZonesByTemperatureType(Zone.TemperatureType type) {
        return zoneRepository.findByTemperatureType(type);
    }
    
    @Override
    @Transactional
    public void deleteZone(Long zoneId) {
        Zone zone = getZoneById(zoneId);
        if (zone != null) {
            zoneRepository.delete(zone);
        }
    }
    
    @Override
    public boolean isZoneNameUnique(String zoneName) {
        return !zoneRepository.existsByZoneName(zoneName);
    }
}