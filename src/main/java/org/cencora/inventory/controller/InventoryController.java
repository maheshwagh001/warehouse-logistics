package org.cencora.inventory.controller;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.inventory.entity.InventoryStock;
import org.cencora.inventory.service.InventoryService;
import org.cencora.inventory.service.dto.InventoryStockDTO;
import org.cencora.inventory.service.dto.StockAdjustmentDTO;

import java.util.List;

@Path("/api/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Inventory Management", description = "Manage inventory stock levels, reservations, and adjustments")
public class InventoryController {
    
    @Inject
    InventoryService inventoryService;
    
    @POST
    @Path("/stock")
    @Operation(summary = "Add inventory stock",
               description = "Add new stock to inventory for a product")
    public Response addStock(InventoryStockDTO stockDTO) {
        try {
            InventoryStock stock = inventoryService.addStock(stockDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(stock)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/stock")
    @Operation(summary = "Get all inventory stock", 
               description = "Retrieve all inventory stock records")
    public Response getAllStock() {
        List<InventoryStock> stock = inventoryService.getAllStock();
        return Response.ok(stock).build();
    }
    
    @GET
    @Path("/stock/{id}")
    @Operation(summary = "Get stock by ID", 
               description = "Get inventory stock details by ID")
    public Response getStockById(@PathParam("id") Long id) {
        InventoryStock stock = inventoryService.getStockById(id);
        if (stock == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Stock not found with ID: " + id))
                    .build();
        }
        return Response.ok(stock).build();
    }
    
    @GET
    @Path("/product/{productId}")
    @Operation(summary = "Get stock by product", 
               description = "Get all inventory stock for a specific product")
    public Response getStockByProduct(@PathParam("productId") Long productId) {
        List<InventoryStock> stock = inventoryService.getStockByProductId(productId);
        return Response.ok(stock).build();
    }
    
    @GET
    @Path("/product/{productId}/available")
    @Operation(summary = "Get available quantity", 
               description = "Get total available quantity for a product")
    public Response getAvailableQuantity(@PathParam("productId") Long productId) {
        Integer availableQty = inventoryService.getAvailableQuantity(productId);
        return Response.ok(new QuantityResponse(productId, availableQty)).build();
    }
    
    @GET
    @Path("/location/{zoneId}/{palletId}")
    @Operation(summary = "Get stock by location", 
               description = "Get inventory stock at specific location")
    public Response getStockByLocation(@PathParam("zoneId") Integer zoneId,
                                      @PathParam("palletId") Integer palletId) {
        List<InventoryStock> stock = inventoryService.getStockByLocation(zoneId, palletId);
        return Response.ok(stock).build();
    }
    
    @GET
    @Path("/low-stock")
    @Operation(summary = "Get low stock items", 
               description = "Get items with stock below specified threshold")
    public Response getLowStockItems(@QueryParam("threshold") @DefaultValue("10") Integer threshold) {
        List<InventoryStock> lowStock = inventoryService.getLowStockItems(threshold);
        return Response.ok(lowStock).build();
    }
    
    @GET
    @Path("/expiring-soon")
    @Operation(summary = "Get expiring soon stock", 
               description = "Get stock expiring within specified days")
    public Response getExpiringSoon(@QueryParam("days") @DefaultValue("30") int days) {
        List<InventoryStock> expiringSoon = inventoryService.getExpiringSoon(days);
        return Response.ok(expiringSoon).build();
    }
    
    @GET
    @Path("/search")
    @Operation(summary = "Search stock", 
               description = "Search inventory stock by various criteria")
    public Response searchStock(@QueryParam("productId") Long productId,
                               @QueryParam("batchNumber") String batchNumber,
                               @QueryParam("status") String status,
                               @QueryParam("zoneId") Integer zoneId,
                               @QueryParam("palletId") Integer palletId) {
        
        List<InventoryStock> results;
        
        if (productId != null && batchNumber != null) {
            InventoryStock stock = inventoryService.getStockByProductAndBatch(productId, batchNumber);
            if (stock != null) {
                results = List.of(stock);
            } else {
                results = List.of();
            }
        } else if (productId != null) {
            results = inventoryService.getStockByProductId(productId);
        } else if (status != null) {
            results = inventoryService.getStockByStatus(status);
        } else if (zoneId != null && palletId != null) {
            results = inventoryService.getStockByLocation(zoneId, palletId);
        } else {
            results = inventoryService.getAllStock();
        }
        
        return Response.ok(results).build();
    }
    
    @POST
    @Path("/adjust")
    @Operation(summary = "Adjust stock",
               description = "Adjust stock quantity (add, remove, reserve, release, etc.)")
    public Response adjustStock(StockAdjustmentDTO adjustment) {
        try {
            InventoryStock stock = inventoryService.adjustStock(adjustment);
            return Response.ok(stock).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/reserve")
    @Operation(summary = "Reserve stock for order", 
               description = "Reserve stock for customer order (FEFO logic)")
    public Response reserveStockForOrder(@QueryParam("productId") Long productId,
                                        @QueryParam("quantity") Integer quantity) {
        try {
            InventoryStock reservedStock = inventoryService.reserveStockForOrder(productId, quantity);
            if (reservedStock == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse("Insufficient stock available"))
                        .build();
            }
            return Response.ok(reservedStock).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/transfer/{stockId}")
    @Operation(summary = "Transfer stock", 
               description = "Transfer stock to different location")
    public Response transferStock(@PathParam("stockId") Long stockId,
                                 @QueryParam("tozoneId") Integer tozoneId,
                                 @QueryParam("topalletId") Integer topalletId) {
        try {
            InventoryStock stock = inventoryService.transferStock(stockId, tozoneId, topalletId);
            return Response.ok(stock).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/stock/{id}")
    @Operation(summary = "Update stock", 
               description = "Update inventory stock information")
    public Response updateStock(@PathParam("id") Long id, InventoryStockDTO stockDTO) {
        try {
            InventoryStock stock = inventoryService.updateStock(id, stockDTO);
            return Response.ok(stock).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/stock/{id}")
    @Operation(summary = "Delete stock", 
               description = "Delete inventory stock record")
    public Response deleteStock(@PathParam("id") Long id) {
        try {
            inventoryService.deleteStock(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/snapshot/{productId}")
    @Operation(summary = "Get inventory snapshot", 
               description = "Get complete inventory snapshot for a product")
    public Response getInventorySnapshot(@PathParam("productId") Long productId) {
        InventoryStock snapshot = inventoryService.getInventorySnapshot(productId);
        if (snapshot == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No inventory found for product: " + productId))
                    .build();
        }
        return Response.ok(snapshot).build();
    }
    
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Get stock by status", 
               description = "Get inventory stock by status (AVAILABLE, RESERVED, DAMAGED, EXPIRED)")
    public Response getStockByStatus(@PathParam("status") String status) {
        List<InventoryStock> stock = inventoryService.getStockByStatus(status);
        return Response.ok(stock).build();
    }
    
    // Add missing methods to InventoryService interface
//    public interface InventoryService {
//        List<InventoryStock> getAllStock();
//        InventoryStock updateStock(Long stockId, InventoryStockDTO stockDTO);
//        List<InventoryStock> getStockByStatus(String status);
//        // ... other methods
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
    
    public static class QuantityResponse {
        private Long productId;
        private Integer availableQuantity;
        private long timestamp;
        
        public QuantityResponse(Long productId, Integer availableQuantity) {
            this.productId = productId;
            this.availableQuantity = availableQuantity;
            this.timestamp = System.currentTimeMillis();
        }
        
        public Long getProductId() { return productId; }
        public Integer getAvailableQuantity() { return availableQuantity; }
        public long getTimestamp() { return timestamp; }
    }
}