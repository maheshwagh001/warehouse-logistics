package org.cencora.warehouse.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.warehouse.service.ZoneService;
import org.cencora.warehouse.entity.Zone;
import org.cencora.warehouse.service.dto.ZoneDTO;
import java.util.List;

@Path("/api/warehouse/zones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Zone Management", description = "Manage warehouse storage zones")
public class ZoneController {
    
    @Inject
    ZoneService zoneService;
    
    @POST
    @Operation(summary = "Create zone",
               description = "Create a new storage zone")
    public Response createZone(ZoneDTO zoneDTO) {
        try {
            Zone zone = zoneService.createZone(zoneDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(zone)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all zones", 
               description = "Get all storage zones")
    public Response getAllZones() {
        List<Zone> zones = zoneService.getAllZones();
        return Response.ok(zones).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get zone by ID", 
               description = "Get zone details by ID")
    public Response getZoneById(@PathParam("id") Long id) {
        Zone zone = zoneService.getZoneById(id);
        if (zone == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Zone not found with ID: " + id))
                    .build();
        }
        return Response.ok(zone).build();
    }
    
    @GET
    @Path("/name/{name}")
    @Operation(summary = "Get zone by name", 
               description = "Get zone details by zone name")
    public Response getZoneByName(@PathParam("name") String name) {
        Zone zone = zoneService.getZoneByName(name);
        if (zone == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Zone not found: " + name))
                    .build();
        }
        return Response.ok(zone).build();
    }
    
    @GET
    @Path("/temperature/{type}")
    @Operation(summary = "Get zones by temperature type", 
               description = "Get zones by temperature type (AMBIENT, COLD, FROZEN, SECURE)")
    public Response getZonesByTemperatureType(@PathParam("type") String type) {
        try {
            Zone.TemperatureType tempType = Zone.TemperatureType.valueOf(type.toUpperCase());
            List<Zone> zones = zoneService.getZonesByTemperatureType(tempType);
            return Response.ok(zones).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid temperature type: " + type))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update zone", 
               description = "Update zone details")
    public Response updateZone(@PathParam("id") Long id, ZoneDTO zoneDTO) {
        try {
            Zone zone = zoneService.updateZone(id, zoneDTO);
            return Response.ok(zone).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete zone", 
               description = "Delete a zone")
    public Response deleteZone(@PathParam("id") Long id) {
        try {
            zoneService.deleteZone(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/check-name/{name}")
    @Operation(summary = "Check zone name uniqueness", 
               description = "Check if zone name is unique")
    public Response checkZoneNameUnique(@PathParam("name") String name) {
        boolean isUnique = zoneService.isZoneNameUnique(name);
        return Response.ok(new UniqueCheckResponse(name, isUnique)).build();
    }
    
    // Response classes
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class UniqueCheckResponse {
        private String name;
        private boolean isUnique;
        
        public UniqueCheckResponse(String name, boolean isUnique) {
            this.name = name;
            this.isUnique = isUnique;
        }
        
        public String getName() { return name; }
        public boolean isUnique() { return isUnique; }
    }
}