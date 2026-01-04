package org.cencora.product.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.product.entity.BatchExpiryTracking;
import org.cencora.product.service.BatchExpiryService;
import org.cencora.product.service.dto.BatchExpiryDTO;

import java.util.List;

@Path("/api/batch-expiry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Batch & Expiry Tracking", description = "Track batch numbers and expiry dates for medical compliance")
public class BatchExpiryController {
    
    @Inject
    BatchExpiryService batchExpiryService;
    
    @POST
    @Operation(summary = "Register new batch",
               description = "Register a new batch with expiry date for product tracking")
    public Response registerBatch(BatchExpiryDTO batchDTO) {
        try {
            BatchExpiryTracking batch = batchExpiryService.registerBatch(batchDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(batch)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all batches", 
               description = "Retrieve all batch expiry tracking records")
    public Response getAllBatches() {
        List<BatchExpiryTracking> batches = batchExpiryService.getAllBatches();
        return Response.ok(batches).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get batch by ID", 
               description = "Get batch expiry details by batch ID")
    public Response getBatchById(@PathParam("id") Long id) {
        BatchExpiryTracking batch = batchExpiryService.getBatchById(id);
        if (batch == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Batch not found with ID: " + id))
                    .build();
        }
        return Response.ok(batch).build();
    }
    
    @GET
    @Path("/batch/{batchNumber}")
    @Operation(summary = "Get batch by batch number", 
               description = "Get batch details by batch number")
    public Response getBatchByNumber(@PathParam("batchNumber") String batchNumber) {
        try {
            BatchExpiryTracking batch = batchExpiryService.getBatchByNumber(batchNumber);
            return Response.ok(batch).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/product/{productId}")
    @Operation(summary = "Get batches by product", 
               description = "Get all batches for a specific product")
    public Response getBatchesByProduct(@PathParam("productId") Long productId) {
        List<BatchExpiryTracking> batches = batchExpiryService.getBatchesByProductId(productId);
        return Response.ok(batches).build();
    }
    
    @GET
    @Path("/expiring-soon")
    @Operation(summary = "Get expiring soon batches", 
               description = "Get batches expiring within specified days")
    public Response getExpiringSoon(@QueryParam("days") @DefaultValue("100") int days) {
        List<BatchExpiryTracking> batches = batchExpiryService.getExpiringSoon(days);
        return Response.ok(batches).build();
    }
    
    @GET
    @Path("/expired")
    @Operation(summary = "Get expired batches", 
               description = "Get all expired batches")
    public Response getExpiredBatches() {
        List<BatchExpiryTracking> batches = batchExpiryService.getExpiredBatches();
        return Response.ok(batches).build();
    }
    
    @GET
    @Path("/location/{zoneId}/{palletId}")
    @Operation(summary = "Get batches by location", 
               description = "Get batches stored at specific location (floor and block)")
    public Response getBatchesByLocation(@PathParam("zoneId") Integer zoneId,
                                        @PathParam("palletId") Integer palletId) {
        List<BatchExpiryTracking> batches = batchExpiryService.getBatchesByLocation(zoneId, palletId);
        return Response.ok(batches).build();
    }
    
    @GET
    @Path("/history/{batchNumber}")
    @Operation(summary = "Get batch history", 
               description = "Get complete history for a batch number")
    public Response getBatchHistory(@PathParam("batchNumber") String batchNumber) {
        List<BatchExpiryTracking> history = batchExpiryService.getBatchHistory(batchNumber);
        return Response.ok(history).build();
    }
    
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Get batches by status", 
               description = "Get batches by status (ACTIVE, EXPIRING_SOON, EXPIRED, QUARANTINED)")
    public Response getBatchesByStatus(@PathParam("status") String status) {
        List<BatchExpiryTracking> batches = batchExpiryService.getBatchesByStatus(status);
        return Response.ok(batches).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update batch", 
               description = "Update batch expiry information")
    public Response updateBatch(@PathParam("id") Long id, BatchExpiryDTO batchDTO) {
        try {
            BatchExpiryTracking batch = batchExpiryService.updateBatch(id, batchDTO);
            return Response.ok(batch).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/adjust-quantity")
    @Operation(summary = "Adjust batch quantity", 
               description = "Adjust quantity for a batch (add or remove)")
    public Response adjustQuantity(@PathParam("id") Long id,
                                  @QueryParam("quantity") Integer quantity) {
        try {
            BatchExpiryTracking batch = batchExpiryService.adjustQuantity(id, quantity);
            return Response.ok(batch).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/mark-expired")
    @Operation(summary = "Mark batch as expired", 
               description = "Manually mark a batch as expired")
    public Response markAsExpired(@PathParam("id") Long id) {
        try {
            batchExpiryService.markAsExpired(id);
            return Response.ok(new MessageResponse("Batch marked as expired")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/quarantine")
    @Operation(summary = "Quarantine batch", 
               description = "Quarantine a batch (quality hold)")
    public Response quarantineBatch(@PathParam("id") Long id) {
        try {
            batchExpiryService.quarantineBatch(id);
            return Response.ok(new MessageResponse("Batch quarantined")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete batch", 
               description = "Delete a batch tracking record")
    public Response deleteBatch(@PathParam("id") Long id) {
        try {
            batchExpiryService.deleteBatch(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/update-statuses")
    @Operation(summary = "Update batch statuses", 
               description = "Update statuses for all batches (run as scheduled job)")
    public Response updateBatchStatuses() {
        try {
            batchExpiryService.updateBatchStatuses();
            return Response.ok(new MessageResponse("Batch statuses updated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
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