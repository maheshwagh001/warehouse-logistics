package org.cencora.product.controller;

import jakarta.inject.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.cencora.Operation;
import org.cencora.Tag;
import org.cencora.Valid;
import org.cencora.product.entity.Product;
import org.cencora.product.service.ProductService;
import org.cencora.product.service.dto.ProductDTO;


import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product Management", description = "Operations for managing products and SKUs")
public class ProductController {
    
    @Inject
    ProductService productService;
    
    @POST
    @Operation(summary = "Add new product with SKU",
               description = "Create a new product with detailed attributes including SKU, category, storage requirements, etc.")
    public Response createProduct(ProductDTO productDTO) {
        try {
            Product product = productService.createProduct(productDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(product)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Operation(summary = "Get all products", 
               description = "Retrieve all products with pagination")
    public Response getAllProducts(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<Product> products = productService.getAllProducts(page, size);
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Get product by ID", 
               description = "Retrieve product details by product ID")
    public Response getProductById(@PathParam("id") Long id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Product not found with ID: " + id))
                        .build();
            }
            return Response.ok(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/sku/{sku}")
    @Operation(summary = "Get product by SKU", 
               description = "Retrieve product details by SKU")
    public Response getProductBySku(@PathParam("sku") String sku) {
        try {
            Product product = productService.getProductBySku(sku);
            return Response.ok(product).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/category/{category}")
    @Operation(summary = "Get products by category", 
               description = "Retrieve products by category")
    public Response getProductsByCategory(@PathParam("category") String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/search/{keyword}")
    @Operation(summary = "Search products", 
               description = "Search products by keyword")
    public Response searchProducts(@PathParam("keyword") String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/controlled-substances")
    @Operation(summary = "Get controlled substances", 
               description = "Retrieve all controlled substances")
    public Response getControlledSubstances() {
        List<Product> products = productService.getControlledSubstances();
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/prescription-required")
    @Operation(summary = "Get prescription required products", 
               description = "Retrieve products requiring prescription")
    public Response getPrescriptionRequiredProducts() {
        List<Product> products = productService.getProductsRequiringPrescription();
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/storage-zone/{zone}")
    @Operation(summary = "Get products by storage zone", 
               description = "Retrieve products by storage zone")
    public Response getProductsByStorageZone(@PathParam("zone") String zone) {
        List<Product> products = productService.getProductsByStorageZone(zone);
        return Response.ok(products).build();
    }
    
    @GET
    @Path("/sku/{sku}/check-unique")
    @Operation(summary = "Check SKU uniqueness", 
               description = "Check if a SKU is unique")
    public Response checkSkuUnique(@PathParam("sku") String sku) {
        boolean isUnique = productService.isSkuUnique(sku);
        return Response.ok(new SkuCheckResponse(sku, isUnique)).build();
    }

    @GET
    @Path("/needing-reorder")
    @Operation(summary = "Get products needing reorder",
            description = "Get products that are below their reorder point")
    public Response getProductsNeedingReorder() {
        List<Product> products = productService.getProductsNeedingReorder();
        return Response.ok(products).build();
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Update product", 
               description = "Update product details")
    public Response updateProduct(@PathParam("id") Long id, ProductDTO productDTO) {
        try {
            Product product = productService.updateProduct(id, productDTO);
            return Response.ok(product).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @PATCH
    @Path("/{id}/attributes")
    @Operation(summary = "Update product attributes", 
               description = "Update specific product attributes")
    public Response updateProductAttributes(@PathParam("id") Long id, ProductDTO productDTO) {
        try {
            Product product = productService.updateProductAttributes(id, productDTO);
            return Response.ok(product).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete product", 
               description = "Delete a product")
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            productService.deleteProduct(id);
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
    
    public static class SkuCheckResponse {
        private String sku;
        private boolean isUnique;
        
        public SkuCheckResponse(String sku, boolean isUnique) {
            this.sku = sku;
            this.isUnique = isUnique;
        }
        
        public String getSku() { return sku; }
        public boolean isUnique() { return isUnique; }
    }
}