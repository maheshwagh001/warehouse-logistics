package org.cencora.inventory.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.inventory.entity.LowStockAlert;
import org.cencora.inventory.service.AlertService;

import java.util.List;

@Path("/api/alerts")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Stock Alerts", description = "Manage low stock alerts and notifications")
public class AlertController {
    
    @Inject
    AlertService alertService;
    
    @GET
    @Path("/low-stock")
    @Operation(summary = "Get low stock alerts",
               description = "Get all active low stock alerts")
    public Response getLowStockAlerts() {
        List<LowStockAlert> alerts = alertService.getActiveLowStockAlerts();
        return Response.ok(alerts).build();
    }
    
    @GET
    @Path("/low-stock/active")
    @Operation(summary = "Get active alerts", 
               description = "Get currently active low stock alerts")
    public Response getActiveAlerts() {
        List<LowStockAlert> alerts = alertService.getActiveAlerts();
        return Response.ok(alerts).build();
    }
    
    @GET
    @Path("/low-stock/resolved")
    @Operation(summary = "Get resolved alerts", 
               description = "Get resolved low stock alerts")
    public Response getResolvedAlerts() {
        List<LowStockAlert> alerts = alertService.getResolvedAlerts();
        return Response.ok(alerts).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get alert by ID", 
               description = "Get low stock alert details by ID")
    public Response getAlertById(@PathParam("id") Long id) {
        LowStockAlert alert = alertService.getAlertById(id);
        if (alert == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Alert not found with ID: " + id))
                    .build();
        }
        return Response.ok(alert).build();
    }
    
    @GET
    @Path("/product/{productId}")
    @Operation(summary = "Get alerts by product", 
               description = "Get low stock alerts for a specific product")
    public Response getAlertsByProduct(@PathParam("productId") Long productId) {
        List<LowStockAlert> alerts = alertService.getAlertsByProductId(productId);
        return Response.ok(alerts).build();
    }
    
    @POST
    @Path("/check")
    @Operation(summary = "Check for alerts", 
               description = "Check inventory and generate low stock alerts")
    public Response checkForAlerts() {
        try {
            List<LowStockAlert> generatedAlerts = alertService.checkAndGenerateAlerts();
            return Response.ok(generatedAlerts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/resolve")
    @Operation(summary = "Resolve alert", 
               description = "Mark a low stock alert as resolved")
    public Response resolveAlert(@PathParam("id") Long id) {
        try {
            alertService.resolveAlert(id);
            return Response.ok(new MessageResponse("Alert resolved successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/acknowledge")
    @Operation(summary = "Acknowledge alert", 
               description = "Acknowledge a low stock alert")
    public Response acknowledgeAlert(@PathParam("id") Long id) {
        try {
            alertService.acknowledgeAlert(id);
            return Response.ok(new MessageResponse("Alert acknowledged")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete alert", 
               description = "Delete a low stock alert")
    public Response deleteAlert(@PathParam("id") Long id) {
        try {
            alertService.deleteAlert(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/old/resolved")
    @Operation(summary = "Clean old resolved alerts", 
               description = "Delete old resolved alerts (cleanup)")
    public Response cleanOldResolvedAlerts(@QueryParam("days") @DefaultValue("30") int days) {
        try {
            int deletedCount = alertService.cleanOldResolvedAlerts(days);
            return Response.ok(new CleanupResponse(deletedCount, "alerts deleted")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    // AlertService interface (needs to be created)
//    public interface AlertService {
//        List<LowStockAlert> getActiveLowStockAlerts();
//        List<LowStockAlert> getActiveAlerts();
//        List<LowStockAlert> getResolvedAlerts();
//        LowStockAlert getAlertById(Long id);
//        List<LowStockAlert> getAlertsByProductId(Long productId);
//        List<LowStockAlert> checkAndGenerateAlerts();
//        void resolveAlert(Long id);
//        void acknowledgeAlert(Long id);
//        void deleteAlert(Long id);
//        int cleanOldResolvedAlerts(int days);
//    }
    
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
    
    public static class CleanupResponse {
        private int count;
        private String action;
        private long timestamp;
        
        public CleanupResponse(int count, String action) {
            this.count = count;
            this.action = action;
            this.timestamp = System.currentTimeMillis();
        }
        
        public int getCount() { return count; }
        public String getAction() { return action; }
        public long getTimestamp() { return timestamp; }
    }
}