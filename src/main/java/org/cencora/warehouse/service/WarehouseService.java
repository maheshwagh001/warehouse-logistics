package org.cencora.warehouse.service;

import org.cencora.warehouse.entity.WarehouseEntity;

import java.util.List;

public interface WarehouseService {

    WarehouseEntity create(WarehouseEntity warehouse);

    List<WarehouseEntity> getAll();

    WarehouseEntity getById(Long id);

    WarehouseEntity update(Long id, WarehouseEntity warehouse);

    void delete(Long id);
}
