package org.cencora.product.service;

import org.cencora.product.entity.Product;
import org.cencora.product.service.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    
    // Create
    Product createProduct(ProductDTO productDTO);
    
    // Read
    Product getProductById(Long productId);
    Product getProductBySku(String sku);
    List<Product> getAllProducts(int page, int size);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String keyword);
    
    // Update
    Product updateProduct(Long productId, ProductDTO productDTO);
    Product updateProductAttributes(Long productId, ProductDTO productDTO);
    
    // Delete
    void deleteProduct(Long productId);
    
    // Business Logic
    List<Product> getControlledSubstances();
    List<Product> getProductsRequiringPrescription();
    List<Product> getProductsByStorageZone(String zone);
    boolean validateProductAttributes(ProductDTO productDTO);
    boolean isSkuUnique(String sku);
    List<Product> getProductsNeedingReorder();

}