package org.cencora.warehouse.controller;

import org.cencora.warehouse.service.LocationService;
import org.cencora.warehouse.entity.Pallet;
import jakarta.inject.Inject;
import org.cencora.Operation;
import org.cencora.Tag;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/warehouse/locations")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Location Management", description = "Find and manage storage locations")
public class LocationController {
    
    @Inject
    LocationService locationService;
    
    @GET
    @Path("/find-for-product")
    @Operation(summary = "Find pallet for product",
               description = "Find available pallet for a product based on storage requirements")
    public Response findPalletForProduct(
            @QueryParam("productId") Long productId,
            @QueryParam("quantity") @DefaultValue("1") Integer quantity) {
        try {
            Pallet pallet = locationService.findAvailablePalletForProduct(productId, quantity);
            return Response.ok(pallet).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/available")
    @Operation(summary = "Find available pallets", 
               description = "Find available pallets by temperature type")
    public Response findAvailablePallets(
            @QueryParam("temperatureType") String temperatureType,
            @QueryParam("minSpace") Integer minSpace) {
        try {
            List<Pallet> pallets = locationService.findAvailablePallets(temperatureType, minSpace);
            return Response.ok(pallets).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/check-compatibility")
    @Operation(summary = "Check product-zone compatibility", 
               description = "Check if a product can be stored in a zone")
    public Response checkCompatibility(
            @QueryParam("productId") Long productId,
            @QueryParam("zoneId") Long zoneId) {
        boolean compatible = locationService.isProductCompatibleWithZone(productId, zoneId);
        return Response.ok(new CompatibilityResponse(productId, zoneId, compatible)).build();
    }
    
    @GET
    @Path("/zone/{zoneId}/space")
    @Operation(summary = "Get zone space info", 
               description = "Get available and occupied space in a zone")
    public Response getZoneSpaceInfo(@PathParam("zoneId") Long zoneId) {
        Integer availableSpace = locationService.getAvailableSpaceInZone(zoneId);
        Integer occupiedSpace = locationService.getTotalOccupiedSpaceInZone(zoneId);
        
        ZoneSpaceResponse response = new ZoneSpaceResponse();
        response.setZoneId(zoneId);
        response.setAvailableSpace(availableSpace);
        response.setOccupiedSpace(occupiedSpace);
        response.setTotalSpace(availableSpace + occupiedSpace);
        
        return Response.ok(response).build();
    }
    
    @POST
    @Path("/reserve")
    @Operation(summary = "Reserve pallets", 
               description = "Reserve multiple pallets for an operation")
    public Response reservePallets(List<Long> palletIds) {
        try {
            locationService.reservePallets(palletIds);
            return Response.ok(new MessageResponse("Pallets reserved successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/release")
    @Operation(summary = "Release pallets", 
               description = "Release reserved pallets")
    public Response releasePallets(List<Long> palletIds) {
        try {
            locationService.releasePallets(palletIds);
            return Response.ok(new MessageResponse("Pallets released successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
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
    
    public static class MessageResponse {
        private String message;
        private long timestamp;
        
        public MessageResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class CompatibilityResponse {
        private Long productId;
        private Long zoneId;
        private boolean compatible;
        
        public CompatibilityResponse(Long productId, Long zoneId, boolean compatible) {
            this.productId = productId;
            this.zoneId = zoneId;
            this.compatible = compatible;
        }
        
        public Long getProductId() { return productId; }
        public Long getZoneId() { return zoneId; }
        public boolean isCompatible() { return compatible; }
    }
    
    public static class ZoneSpaceResponse {
        private Long zoneId;
        private Integer availableSpace;
        private Integer occupiedSpace;
        private Integer totalSpace;
        
        public Long getZoneId() { return zoneId; }
        public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
        
        public Integer getAvailableSpace() { return availableSpace; }
        public void setAvailableSpace(Integer availableSpace) { this.availableSpace = availableSpace; }
        
        public Integer getOccupiedSpace() { return occupiedSpace; }
        public void setOccupiedSpace(Integer occupiedSpace) { this.occupiedSpace = occupiedSpace; }
        
        public Integer getTotalSpace() { return totalSpace; }
        public void setTotalSpace(Integer totalSpace) { this.totalSpace = totalSpace; }
    }
}