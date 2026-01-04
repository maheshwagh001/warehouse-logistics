package org.cencora.product.service.dto;


import org.cencora.NotNull;

public class ProductDTO {
    
    private Long productId;
    
    @NotNull(message = "SKU is required")
    private String sku;

    @NotNull(message = "Product name is required")
    private String productName;
    
    private String category;
    private String subCategory;
    private String descriptionShort;
    private String descriptionDetailed;
    private String storageZone;
    private Integer shelfLifeMonths;
    private Boolean requiresPrescription;
    private Boolean controlledSubstance;
    private Integer reorderPoint;
    private Integer minStockLevel;
    private Integer targetStockLevel;


    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
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


}