package org.cencora.product.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.product.entity.Product;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    
    public Optional<Product> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }
    
    public List<Product> findByCategory(String category) {
        return find("category", category).list();
    }
    
    public List<Product> findByStorageZone(String storageZone) {
        return find("storageZone = :zone", Parameters.with("zone", storageZone)).list();
    }
    
    public List<Product> findControlledSubstances() {
        return find("controlledSubstance = true").list();
    }
    
    public List<Product> findRequiringPrescription() {
        return find("requiresPrescription = true").list();
    }
    
    public List<Product> searchByKeyword(String keyword) {
        return find("productName LIKE :keyword OR sku LIKE :keyword OR descriptionShort LIKE :keyword", 
                   Parameters.with("keyword", "%" + keyword + "%")).list();
    }
    
    public List<Product> findExpiringSoonProducts(int monthsThreshold) {
        // For products with shelf life expiring soon
        return find("shelfLifeMonths <= :threshold and shelfLifeMonths > 0", 
                   Parameters.with("threshold", monthsThreshold)).list();
    }
    
    public boolean existsBySku(String sku) {
        return count("sku = :sku", Parameters.with("sku", sku)) > 0;
    }
    
    public List<Product> findBySkuList(List<String> skus) {
        return find("sku in :skus", Parameters.with("skus", skus)).list();
    }
    public List<Product> findProductsNeedingReorder() {
        // Products with non-null reorder point
        return find("reorderPoint is not null").list();
    }




}