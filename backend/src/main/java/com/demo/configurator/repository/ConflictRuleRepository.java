package com.demo.configurator.repository;

import com.demo.configurator.model.ConflictRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConflictRuleRepository extends JpaRepository<ConflictRule, Long> {
    List<ConflictRule> findBySourceOptionId(Long sourceOptionId);
}
