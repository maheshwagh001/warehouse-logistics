package org.cencora.product.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "sku", unique = true, nullable = false)
    private String sku;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "sub_category")
    private String subCategory;
    
    @Column(name = "description_short")
    private String descriptionShort;
    
    @Column(name = "description_detailed")
    private String descriptionDetailed;
    
    @Column(name = "storage_zone")
    private String storageZone;
    
    @Column(name = "shelf_life_months")
    private Integer shelfLifeMonths;
    
    @Column(name = "requires_prescription")
    private Boolean requiresPrescription;
    
    @Column(name = "controlled_substance")
    private Boolean controlledSubstance;

    @Column(name = "reorder_point")
    private Integer reorderPoint;  // When to trigger reorder

    @Column(name = "min_stock_level")
    private Integer minStockLevel;  // Absolute minimum

    @Column(name = "target_stock_level")
    private Integer targetStockLevel;  // Ideal stock level
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.requiresPrescription = false;
        this.controlledSubstance = false;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    
    public String getDescriptionShort() { return descriptionShort; }
    public void setDescriptionShort(String descriptionShort) { this.descriptionShort = descriptionShort; }
    
    public String getDescriptionDetailed() { return descriptionDetailed; }
    public void setDescriptionDetailed(String descriptionDetailed) { this.descriptionDetailed = descriptionDetailed; }
    
    public String getStorageZone() { return storageZone; }
    public void setStorageZone(String storageZone) { this.storageZone = storageZone; }
    
    public Integer getShelfLifeMonths() { return shelfLifeMonths; }
    public void setShelfLifeMonths(Integer shelfLifeMonths) { this.shelfLifeMonths = shelfLifeMonths; }
    
    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(Boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
    
    public Boolean getControlledSubstance() { return controlledSubstance; }
    public void setControlledSubstance(Boolean controlledSubstance) { this.controlledSubstance = controlledSubstance; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public Integer getTargetStockLevel() { return targetStockLevel; }
    public void setTargetStockLevel(Integer targetStockLevel) { this.targetStockLevel = targetStockLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}