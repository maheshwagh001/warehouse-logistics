package org.cencora.product.service.dto;

import org.cencora.NotNull;
import org.cencora.Positive;

public class ReplenishmentRuleDTO {
    
    private Long ruleId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Threshold quantity is required")
    @Positive(message = "Threshold quantity must be positive")
    private Integer thresholdQuantity;
    
    @NotNull(message = "Reorder quantity is required")
    @Positive(message = "Reorder quantity must be positive")
    private Integer reorderQuantity;
    
    private Boolean isActive = true;
    
    // Getters and Setters
    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Integer getThresholdQuantity() { return thresholdQuantity; }
    public void setThresholdQuantity(Integer thresholdQuantity) { this.thresholdQuantity = thresholdQuantity; }
    
    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}