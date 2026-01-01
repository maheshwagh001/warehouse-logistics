package org.cencora.warehouse.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.warehouse.entity.WarehouseEntity;
import org.cencora.warehouse.repository.WarehouseRepository;

import java.util.List;

@ApplicationScoped
public class WarehouseServiceImpl implements WarehouseService {

    @Inject
    WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseEntity create(WarehouseEntity warehouse) {
        warehouseRepository.persist(warehouse);
        return warehouse;
    }

    public List<WarehouseEntity> getAll() {
        return warehouseRepository.listAll();
    }

    public WarehouseEntity getById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Transactional
    public WarehouseEntity update(Long id, WarehouseEntity warehouse) {
        WarehouseEntity existing = warehouseRepository.findById(id);
        if (existing == null) {
            return null;
        }

        existing.setName(warehouse.getName());
        existing.setAddress(warehouse.getAddress());
        existing.setLatitude(warehouse.getLatitude());
        existing.setLongitude(warehouse.getLongitude());
        existing.setContactPerson(warehouse.getContactPerson());
        existing.setContactNumber(warehouse.getContactNumber());

        return existing;
    }

    @Transactional
    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }
}
