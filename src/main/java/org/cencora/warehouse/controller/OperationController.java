package org.cencora.warehouse.controller;



import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.warehouse.entity.WarehouseOperation;
import org.cencora.warehouse.service.OperationService;
import org.cencora.warehouse.service.dto.OperationDTO;
import org.cencora.warehouse.service.dto.OperationItemDTO;

import java.util.List;

@Path("/api/warehouse/operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Warehouse Operations", description = "Manage all warehouse operations")
public class OperationController {
    
    @Inject
    OperationService operationService;
    
    // Generic operation endpoints
    
    @POST
    @Operation(summary = "Create operation",
               description = "Create a new warehouse operation")
    public Response createOperation( OperationDTO operationDTO) {
        try {
            WarehouseOperation operation = operationService.createOperation(operationDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(operation)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all operations", 
               description = "Get all warehouse operations")
    public Response getAllOperations() {
        List<WarehouseOperation> operations = operationService.getPendingOperations();
        return Response.ok(operations).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get operation by ID", 
               description = "Get operation details by ID")
    public Response getOperationById(@PathParam("id") Long id) {
        WarehouseOperation operation = operationService.getOperationById(id);
        if (operation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Operation not found with ID: " + id))
                    .build();
        }
        return Response.ok(operation).build();
    }
    
    @GET
    @Path("/number/{number}")
    @Operation(summary = "Get operation by number", 
               description = "Get operation by operation number")
    public Response getOperationByNumber(@PathParam("number") String number) {
        WarehouseOperation operation = operationService.getOperationByNumber(number);
        if (operation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Operation not found: " + number))
                    .build();
        }
        return Response.ok(operation).build();
    }
    
    @GET
    @Path("/type/{type}")
    @Operation(summary = "Get operations by type", 
               description = "Get operations by type (INBOUND, PICKING, PACKING, RETURN, TRANSFER)")
    public Response getOperationsByType(@PathParam("type") String type) {
        try {
            WarehouseOperation.OperationType opType = 
                WarehouseOperation.OperationType.valueOf(type.toUpperCase());
            List<WarehouseOperation> operations = operationService.getOperationsByType(opType);
            return Response.ok(operations).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid operation type: " + type))
                    .build();
        }
    }
    
    @GET
    @Path("/status/{status}")
    @Operation(summary = "Get operations by status", 
               description = "Get operations by status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED, ON_HOLD)")
    public Response getOperationsByStatus(@PathParam("status") String status) {
        try {
            WarehouseOperation.OperationStatus opStatus = 
                WarehouseOperation.OperationStatus.valueOf(status.toUpperCase());
            // This would need a repository method, for now use existing
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid operation status: " + status))
                    .build();
        }
    }
    
    @GET
    @Path("/pending")
    @Operation(summary = "Get pending operations", 
               description = "Get all pending operations")
    public Response getPendingOperations() {
        List<WarehouseOperation> operations = operationService.getPendingOperations();
        return Response.ok(operations).build();
    }
    
    @GET
    @Path("/in-progress")
    @Operation(summary = "Get in-progress operations", 
               description = "Get all operations in progress")
    public Response getInProgressOperations() {
        List<WarehouseOperation> operations = operationService.getInProgressOperations();
        return Response.ok(operations).build();
    }
    
    @GET
    @Path("/reference/{ref}")
    @Operation(summary = "Get operations by reference", 
               description = "Get operations by reference number (PO, Order, Return)")
    public Response getOperationsByReference(@PathParam("ref") String ref) {
        List<WarehouseOperation> operations = operationService.getOperationsByReference(ref);
        return Response.ok(operations).build();
    }
    
    // Operation lifecycle endpoints
    
    @PATCH
    @Path("/{id}/start")
    @Operation(summary = "Start operation", 
               description = "Start a pending operation")
    public Response startOperation(@PathParam("id") Long id) {
        try {
            WarehouseOperation operation = operationService.startOperation(id);
            return Response.ok(operation).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/complete")
    @Operation(summary = "Complete operation", 
               description = "Complete an in-progress operation")
    public Response completeOperation(@PathParam("id") Long id) {
        try {
            WarehouseOperation operation = operationService.completeOperation(id);
            return Response.ok(operation).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/cancel")
    @Operation(summary = "Cancel operation", 
               description = "Cancel an operation")
    public Response cancelOperation(@PathParam("id") Long id) {
        try {
            WarehouseOperation operation = operationService.cancelOperation(id);
            return Response.ok(operation).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/hold")
    @Operation(summary = "Hold operation", 
               description = "Put an operation on hold")
    public Response holdOperation(@PathParam("id") Long id) {
        try {
            WarehouseOperation operation = operationService.holdOperation(id);
            return Response.ok(operation).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    // Specific operation creation endpoints
    
    @POST
    @Path("/inbound")
    @Operation(summary = "Create inbound operation", 
               description = "Create an inbound operation for PO receiving")
    public Response createInboundOperation(
            @QueryParam("poNumber") String poNumber,
            List<OperationItemDTO> items) {
        try {
            WarehouseOperation operation = operationService.createInboundOperation(poNumber, items);
            return Response.status(Response.Status.CREATED)
                    .entity(operation)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/picking")
    @Operation(summary = "Create picking operation", 
               description = "Create a picking operation for order fulfillment")
    public Response createPickingOperation(
            @QueryParam("orderNumber") String orderNumber,
            List<OperationItemDTO> items) {
        try {
            WarehouseOperation operation = operationService.createPickingOperation(orderNumber, items);
            return Response.status(Response.Status.CREATED)
                    .entity(operation)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @POST
    @Path("/return")
    @Operation(summary = "Create return operation", 
               description = "Create a return operation for returns processing")
    public Response createReturnOperation(
            @QueryParam("returnNumber") String returnNumber,
            List<OperationItemDTO> items) {
        try {
            WarehouseOperation operation = operationService.createReturnOperation(returnNumber, items);
            return Response.status(Response.Status.CREATED)
                    .entity(operation)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    // Process operations (business logic)
    
    @POST
    @Path("/{id}/process")
    @Operation(summary = "Process operation", 
               description = "Process an operation (execute business logic)")
    public Response processOperation(@PathParam("id") Long id,
                                    @QueryParam("type") String type) {
        try {
            switch (type.toUpperCase()) {
                case "INBOUND":
                    operationService.processInboundOperation(id);
                    break;
                case "PICKING":
                    operationService.processPickingOperation(id);
                    break;
                case "RETURN":
                    operationService.processReturnOperation(id);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operation type: " + type);
            }
            return Response.ok(new MessageResponse("Operation processed successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update operation", 
               description = "Update operation details")
    public Response updateOperation(@PathParam("id") Long id, OperationDTO operationDTO) {
        try {
            WarehouseOperation operation = operationService.updateOperation(id, operationDTO);
            return Response.ok(operation).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete operation", 
               description = "Delete an operation")
    public Response deleteOperation(@PathParam("id") Long id) {
        try {
            operationService.deleteOperation(id);
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