package org.cencora.product.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.cencora.product.entity.ReplenishmentRule;

import java.util.List;

@ApplicationScoped
public class ReplenishmentRuleRepository implements PanacheRepository<ReplenishmentRule> {
    
    public List<ReplenishmentRule> findByProductId(Long productId) {
        return find("product.productId = :productId", 
                   Parameters.with("productId", productId)).list();
    }
    
    public List<ReplenishmentRule> findActiveRules() {
        return find("isActive = true").list();
    }
    
    public List<ReplenishmentRule> findByProductIds(List<Long> productIds) {
        return find("product.productId in :productIds and isActive = true", 
                   Parameters.with("productIds", productIds)).list();
    }
    
    public boolean existsByProductId(Long productId) {
        return count("product.productId = :productId", 
                    Parameters.with("productId", productId)) > 0;
    }
}