package org.cencora.warehouse.controller;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.cencora.warehouse.entity.WarehouseEntity;
import org.cencora.warehouse.service.WarehouseService;

import java.util.List;

@Path("/api/warehouses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseController {

    @Inject
    WarehouseService warehouseService;

    @POST
    public WarehouseEntity create(WarehouseEntity warehouse) {
        return warehouseService.create(warehouse);
    }

    @GET
    public List<WarehouseEntity> getAll() {
        return warehouseService.getAll();
    }

    @GET
    @Path("/{id}")
    public WarehouseEntity getById(@PathParam("id") Long id) {
        return warehouseService.getById(id);
    }

    @PUT
    @Path("/{id}")
    public WarehouseEntity update(@PathParam("id") Long id, WarehouseEntity warehouse) {
        return warehouseService.update(id, warehouse);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        warehouseService.delete(id);
    }
}

