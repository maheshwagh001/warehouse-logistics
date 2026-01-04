package org.cencora.product.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.product.entity.ReplenishmentRule;
import org.cencora.product.service.ReplenishmentService;
import org.cencora.product.service.dto.ReplenishmentRuleDTO;

import java.util.List;

@Path("/api/replenishment-rules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auto-Replenishment Rules", description = "Configure automated reorder triggers when stock falls below defined thresholds")
public class ReplenishmentController {
    
    @Inject
    ReplenishmentService replenishmentService;
    
    @POST
    @Operation(summary = "Create replenishment rule",
               description = "Configure automated reorder trigger for a product")
    public Response createRule(ReplenishmentRuleDTO ruleDTO) {
        try {
            ReplenishmentRule rule = replenishmentService.createRule(ruleDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(rule)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all replenishment rules", 
               description = "Retrieve all active replenishment rules")
    public Response getAllRules() {
        List<ReplenishmentRule> rules = replenishmentService.getAllRules();
        return Response.ok(rules).build();
    }
    
    @GET
    @Path("/product/{productId}")
    @Operation(summary = "Get rules by product", 
               description = "Get replenishment rules for a specific product")
    public Response getRulesByProductId(@PathParam("productId") Long productId) {
        List<ReplenishmentRule> rules = replenishmentService.getRulesByProductId(productId);
        return Response.ok(rules).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get rule by ID", 
               description = "Get replenishment rule details by ID")
    public Response getRuleById(@PathParam("id") Long id) {
        ReplenishmentRule rule = replenishmentService.getRuleById(id);
        if (rule == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Replenishment rule not found with ID: " + id))
                    .build();
        }
        return Response.ok(rule).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update replenishment rule", 
               description = "Update existing replenishment rule")
    public Response updateRule(@PathParam("id") Long id, ReplenishmentRuleDTO ruleDTO) {
        try {
            ReplenishmentRule rule = replenishmentService.updateRule(id, ruleDTO);
            return Response.ok(rule).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete replenishment rule", 
               description = "Delete a replenishment rule")
    public Response deleteRule(@PathParam("id") Long id) {
        try {
            replenishmentService.deleteRule(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/deactivate")
    @Operation(summary = "Deactivate rule", 
               description = "Deactivate a replenishment rule (soft disable)")
    public Response deactivateRule(@PathParam("id") Long id) {
        try {
            replenishmentService.deactivateRule(id);
            return Response.ok(new MessageResponse("Rule deactivated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/activate")
    @Operation(summary = "Activate rule", 
               description = "Activate a previously deactivated replenishment rule")
    public Response activateRule(@PathParam("id") Long id) {
        try {
            replenishmentService.activateRule(id);
            return Response.ok(new MessageResponse("Rule activated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/check/{productId}")
    @Operation(summary = "Check rules against inventory", 
               description = "Check if replenishment rules should trigger for a product")
    public Response checkRules(@PathParam("productId") Long productId,
                              @QueryParam("currentQuantity") Integer currentQuantity) {
        try {
            List<ReplenishmentRule> triggeredRules = 
                replenishmentService.checkRulesAgainstInventory(productId, currentQuantity);
            return Response.ok(triggeredRules).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/check-all")
    @Operation(summary = "Check all rules", 
               description = "Check all replenishment rules (batch operation)")
    public Response checkAllRules() {
        // This would typically be a scheduled job, but we expose it as an endpoint for manual trigger
        List<ReplenishmentRule> triggeredRules = replenishmentService.checkAndTriggerRules();
        return Response.ok(triggeredRules).build();
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