package org.cencora.warehouse.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.warehouse.entity.WarehouseEntity;

@ApplicationScoped
public class WarehouseRepository implements PanacheRepository<WarehouseEntity> {
}

