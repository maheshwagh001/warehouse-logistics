package org.cencora.warehouse.controller;

import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.warehouse.service.PalletService;
import org.cencora.warehouse.entity.Pallet;
import org.cencora.warehouse.service.dto.PalletDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/warehouse/pallets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pallet Management", description = "Manage warehouse pallets")
public class PalletController {
    
    @Inject
    PalletService palletService;
    
    @POST
    @Operation(summary = "Create pallet", 
               description = "Create a new pallet")
    public Response createPallet(PalletDTO palletDTO) {
        try {
            Pallet pallet = palletService.createPallet(palletDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(pallet)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all pallets",
               description = "Get all pallets")
    public Response getAllPallets() {
        List<Pallet> pallets = palletService.getAvailablePallets();
        return Response.ok(pallets).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get pallet by ID", 
               description = "Get pallet details by ID")
    public Response getPalletById(@PathParam("id") Long id) {
        Pallet pallet = palletService.getPalletById(id);
        if (pallet == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Pallet not found with ID: " + id))
                    .build();
        }
        return Response.ok(pallet).build();
    }
    
    @GET
    @Path("/code/{code}")
    @Operation(summary = "Get pallet by code", 
               description = "Get pallet details by pallet code")
    public Response getPalletByCode(@PathParam("code") String code) {
        try {
            Pallet pallet = palletService.getPalletByCode(code);
            return Response.ok(pallet).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/zone/{zoneId}")
    @Operation(summary = "Get pallets by zone", 
               description = "Get all pallets in a zone")
    public Response getPalletsByZone(@PathParam("zoneId") Long zoneId) {
        List<Pallet> pallets = palletService.getPalletsByZone(zoneId);
        return Response.ok(pallets).build();
    }
    
    @GET
    @Path("/available")
    @Operation(summary = "Get available pallets", 
               description = "Get all available pallets")
    public Response getAvailablePallets() {
        List<Pallet> pallets = palletService.getAvailablePallets();
        return Response.ok(pallets).build();
    }
    
    @GET
    @Path("/available/zone/{zoneId}")
    @Operation(summary = "Get available pallets in zone", 
               description = "Get available pallets in a specific zone")
    public Response getAvailablePalletsInZone(
            @PathParam("zoneId") Long zoneId,
            @QueryParam("requiredUnits") @DefaultValue("1") int requiredUnits) {
        
        List<Pallet> pallets = palletService.getAvailablePalletsInZone(zoneId, requiredUnits);
        return Response.ok(pallets).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update pallet", 
               description = "Update pallet details")
    public Response updatePallet(@PathParam("id") Long id, PalletDTO palletDTO) {
        try {
            Pallet pallet = palletService.updatePallet(id, palletDTO);
            return Response.ok(pallet).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/status/{status}")
    @Operation(summary = "Update pallet status", 
               description = "Update pallet status (AVAILABLE, OCCUPIED, RESERVED, DAMAGED, QUARANTINED)")
    public Response updatePalletStatus(@PathParam("id") Long id,
                                      @PathParam("status") String status) {
        try {
            Pallet.PalletStatus palletStatus = Pallet.PalletStatus.valueOf(status.toUpperCase());
            palletService.updatePalletStatus(id, palletStatus);
            return Response.ok(new MessageResponse("Pallet status updated")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/occupancy")
    @Operation(summary = "Update pallet occupancy", 
               description = "Add or remove units from pallet")
    public Response updatePalletOccupancy(@PathParam("id") Long id,
                                         @QueryParam("units") int units) {
        try {
            palletService.updatePalletOccupancy(id, units);
            return Response.ok(new MessageResponse("Pallet occupancy updated")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete pallet", 
               description = "Delete a pallet")
    public Response deletePallet(@PathParam("id") Long id) {
        try {
            palletService.deletePallet(id);
            return Response.noContent().build();
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
}