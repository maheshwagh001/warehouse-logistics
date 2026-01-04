package org.cencora.product.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.product.entity.Product;
import org.cencora.product.repository.ProductRepository;
import org.cencora.product.service.ProductService;
import org.cencora.product.service.dto.ProductDTO;

import java.util.List;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {
    
    @Inject
    ProductRepository productRepository;
    
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        // Validate SKU uniqueness
        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + productDTO.getSku());
        }
        
        // Validate required fields
        validateProductAttributes(productDTO);
        
        // Convert DTO to Entity
        Product product = new Product();
        mapDTOToEntity(productDTO, product);
        
        // Save product
        productRepository.persist(product);
        
        return product;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId);
    }
    
    @Override
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
    }
    
    @Override
    public List<Product> getAllProducts(int page, int size) {
        return productRepository.findAll()
                .page(page, size)
                .list();
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }
    
    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        
        // Check if SKU is being changed and if it's unique
        if (!product.getSku().equals(productDTO.getSku()) && 
            productRepository.existsBySku(productDTO.getSku())) {
            throw new IllegalArgumentException("SKU already exists: " + productDTO.getSku());
        }
        
        validateProductAttributes(productDTO);
        mapDTOToEntity(productDTO, product);
        product.preUpdate();
        
        productRepository.persist(product);
        return product;
    }
    
    @Override
    @Transactional
    public Product updateProductAttributes(Long productId, ProductDTO productDTO) {
        Product product = getProductById(productId);
        
        // Update only non-null attributes from DTO
        if (productDTO.getProductName() != null) {
            product.setProductName(productDTO.getProductName());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }
        if (productDTO.getDescriptionDetailed() != null) {
            product.setDescriptionDetailed(productDTO.getDescriptionDetailed());
        }
        if (productDTO.getStorageZone() != null) {
            product.setStorageZone(productDTO.getStorageZone());
        }
        if (productDTO.getShelfLifeMonths() != null) {
            product.setShelfLifeMonths(productDTO.getShelfLifeMonths());
        }
        if (productDTO.getRequiresPrescription() != null) {
            product.setRequiresPrescription(productDTO.getRequiresPrescription());
        }
        if (productDTO.getControlledSubstance() != null) {
            product.setControlledSubstance(productDTO.getControlledSubstance());
        }
        
        product.preUpdate();
        productRepository.persist(product);
        return product;
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }
    
    @Override
    public List<Product> getControlledSubstances() {
        return productRepository.findControlledSubstances();
    }
    
    @Override
    public List<Product> getProductsRequiringPrescription() {
        return productRepository.findRequiringPrescription();
    }
    
    @Override
    public List<Product> getProductsByStorageZone(String zone) {
        return productRepository.findByStorageZone(zone);
    }
    
    @Override
    public boolean validateProductAttributes(ProductDTO productDTO) {
        if (productDTO.getSku() == null || productDTO.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        
        // Medical compliance validation
        if (Boolean.TRUE.equals(productDTO.getControlledSubstance()) && 
            productDTO.getShelfLifeMonths() == null) {
            throw new IllegalArgumentException("Shelf life is required for controlled substances");
        }
        
        return true;
    }
    
    @Override
    public boolean isSkuUnique(String sku) {
        return !productRepository.existsBySku(sku);
    }

    @Override
    public List<Product> getProductsNeedingReorder() {
        return productRepository.findProductsNeedingReorder();
    }
    
    private void mapDTOToEntity(ProductDTO dto, Product entity) {
        entity.setSku(dto.getSku());
        entity.setProductName(dto.getProductName());
        entity.setCategory(dto.getCategory());
        entity.setSubCategory(dto.getSubCategory());
        entity.setDescriptionShort(dto.getDescriptionShort());
        entity.setDescriptionDetailed(dto.getDescriptionDetailed());
        entity.setStorageZone(dto.getStorageZone());
        entity.setShelfLifeMonths(dto.getShelfLifeMonths());
        entity.setRequiresPrescription(dto.getRequiresPrescription());
        entity.setControlledSubstance(dto.getControlledSubstance());
    }
}