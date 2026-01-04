package org.cencora.product.service.impl;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.cencora.product.entity.Product;
import org.cencora.product.entity.ReplenishmentRule;
import org.cencora.product.repository.ProductRepository;
import org.cencora.product.repository.ReplenishmentRuleRepository;
import org.cencora.product.service.ReplenishmentService;
import org.cencora.product.service.dto.ReplenishmentRuleDTO;

import java.util.List;

@ApplicationScoped
public class ReplenishmentServiceImpl implements ReplenishmentService {
    
    @Inject
    ReplenishmentRuleRepository ruleRepository;
    
    @Inject
    ProductRepository productRepository;
    
    @Override
    @Transactional
    public ReplenishmentRule createRule(ReplenishmentRuleDTO ruleDTO) {
        // Validate product exists
        Product product = productRepository.findById(ruleDTO.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + ruleDTO.getProductId());
        }
        
        // Check if rule already exists for this product
        if (ruleRepository.existsByProductId(ruleDTO.getProductId())) {
            throw new IllegalArgumentException("Replenishment rule already exists for product ID: " + ruleDTO.getProductId());
        }
        
        // Validate quantities
        if (ruleDTO.getThresholdQuantity() <= 0) {
            throw new IllegalArgumentException("Threshold quantity must be greater than 0");
        }
        if (ruleDTO.getReorderQuantity() <= 0) {
            throw new IllegalArgumentException("Reorder quantity must be greater than 0");
        }
        
        // Create rule
        ReplenishmentRule rule = new ReplenishmentRule();
        rule.setProduct(product);
        rule.setThresholdQuantity(ruleDTO.getThresholdQuantity());
        rule.setReorderQuantity(ruleDTO.getReorderQuantity());
        rule.setIsActive(ruleDTO.getIsActive() != null ? ruleDTO.getIsActive() : true);
        
        ruleRepository.persist(rule);
        return rule;
    }

    @Override
    public ReplenishmentRule getRuleById(Long ruleId) {
        return null;
    }

    @Override
    public List<ReplenishmentRule> getAllRules() {
        return ruleRepository.findActiveRules();
    }
    
    @Override
    public List<ReplenishmentRule> getRulesByProductId(Long productId) {
        return ruleRepository.findByProductId(productId);
    }
    
    @Override
    @Transactional
    public ReplenishmentRule updateRule(Long ruleId, ReplenishmentRuleDTO ruleDTO) {
        ReplenishmentRule rule = ruleRepository.findById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("Replenishment rule not found with ID: " + ruleId);
        }
        
        // Update product if changed
        if (!rule.getProduct().getProductId().equals(ruleDTO.getProductId())) {
            Product product = productRepository.findById(ruleDTO.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found with ID: " + ruleDTO.getProductId());
            }
            rule.setProduct(product);
        }
        
        // Update quantities
        if (ruleDTO.getThresholdQuantity() != null) {
            if (ruleDTO.getThresholdQuantity() <= 0) {
                throw new IllegalArgumentException("Threshold quantity must be greater than 0");
            }
            rule.setThresholdQuantity(ruleDTO.getThresholdQuantity());
        }
        
        if (ruleDTO.getReorderQuantity() != null) {
            if (ruleDTO.getReorderQuantity() <= 0) {
                throw new IllegalArgumentException("Reorder quantity must be greater than 0");
            }
            rule.setReorderQuantity(ruleDTO.getReorderQuantity());
        }
        
        if (ruleDTO.getIsActive() != null) {
            rule.setIsActive(ruleDTO.getIsActive());
        }
        
        rule.preUpdate();
        ruleRepository.persist(rule);
        return rule;
    }
    
    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        ReplenishmentRule rule = ruleRepository.findById(ruleId);
        if (rule != null) {
            ruleRepository.delete(rule);
        }
    }
    
    @Override
    @Transactional
    public void deactivateRule(Long ruleId) {
        ReplenishmentRule rule = ruleRepository.findById(ruleId);
        if (rule != null) {
            rule.setIsActive(false);
            rule.preUpdate();
            ruleRepository.persist(rule);
        }
    }
    
    @Override
    @Transactional
    public void activateRule(Long ruleId) {
        ReplenishmentRule rule = ruleRepository.findById(ruleId);
        if (rule != null) {
            rule.setIsActive(true);
            rule.preUpdate();
            ruleRepository.persist(rule);
        }
    }
    
    @Override
    public List<ReplenishmentRule> checkRulesAgainstInventory(Long productId, Integer currentQuantity) {
        // This would typically integrate with inventory service
        // For now, return rules that would trigger based on current quantity
        return ruleRepository.findByProductId(productId)
                .stream()
                .filter(rule -> rule.getIsActive() && currentQuantity <= rule.getThresholdQuantity())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ReplenishmentRule> checkAndTriggerRules() {
        return List.of();
    }
}