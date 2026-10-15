package com.demo.configurator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conflict_rules")
public class ConflictRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_option_id", nullable = false)
    private ProductOption sourceOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_option_id", nullable = false)
    private ProductOption targetOption;

    @Column(nullable = false)
    private String ruleType = "EXCLUDES"; // EXCLUDES or REQUIRES

    public ConflictRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductOption getSourceOption() {
        return sourceOption;
    }

    public void setSourceOption(ProductOption sourceOption) {
        this.sourceOption = sourceOption;
    }

    public ProductOption getTargetOption() {
        return targetOption;
    }

    public void setTargetOption(ProductOption targetOption) {
        this.targetOption = targetOption;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
}
