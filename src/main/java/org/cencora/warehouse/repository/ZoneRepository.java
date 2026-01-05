package org.cencora.warehouse.repository;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.warehouse.entity.Zone;

import java.util.List;

@ApplicationScoped
public class ZoneRepository implements PanacheRepository<Zone> {
    
    public List<Zone> findByTemperatureType(Zone.TemperatureType type) {
        return find("temperatureType = :type", 
                   Parameters.with("type", type)).list();
    }
    
    public Zone findByZoneName(String zoneName) {
        return find("zoneName = :name", 
                   Parameters.with("name", zoneName)).firstResult();
    }
    
    public List<Zone> findAllActive() {
        return listAll();
    }
    
    public boolean existsByZoneName(String zoneName) {
        return count("zoneName = :name", 
                    Parameters.with("name", zoneName)) > 0;
    }
}