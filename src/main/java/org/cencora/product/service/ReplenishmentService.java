package org.cencora.product.service;


import org.cencora.product.entity.ReplenishmentRule;
import org.cencora.product.service.dto.ReplenishmentRuleDTO;

import java.util.List;

public interface ReplenishmentService {
    
    // Create
    ReplenishmentRule createRule(ReplenishmentRuleDTO ruleDTO);
    
    // Read
    ReplenishmentRule getRuleById(Long ruleId);
    List<ReplenishmentRule> getAllRules();
    List<ReplenishmentRule> getRulesByProductId(Long productId);
    
    // Update
    ReplenishmentRule updateRule(Long ruleId, ReplenishmentRuleDTO ruleDTO);
    void activateRule(Long ruleId);
    void deactivateRule(Long ruleId);
    
    // Delete
    void deleteRule(Long ruleId);
    
    // Business Logic
    List<ReplenishmentRule> checkRulesAgainstInventory(Long productId, Integer currentQuantity);
    List<ReplenishmentRule> checkAndTriggerRules();
}