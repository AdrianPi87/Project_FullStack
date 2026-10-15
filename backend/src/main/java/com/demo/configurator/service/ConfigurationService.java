package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.Configuration;
import com.demo.configurator.model.ConflictRule;
import com.demo.configurator.model.OptionGroup;
import com.demo.configurator.model.Product;
import com.demo.configurator.model.ProductOption;
import com.demo.configurator.repository.ConfigurationRepository;
import com.demo.configurator.repository.ConflictRuleRepository;
import com.demo.configurator.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {

    private final ProductRepository productRepository;
    private final ConflictRuleRepository conflictRuleRepository;
    private final ConfigurationRepository configurationRepository;

    public ConfigurationService(ProductRepository productRepository,
                                ConflictRuleRepository conflictRuleRepository,
                                ConfigurationRepository configurationRepository) {
        this.productRepository = productRepository;
        this.conflictRuleRepository = conflictRuleRepository;
        this.configurationRepository = configurationRepository;
    }

    @Transactional
    public Dtos.ConfigurationResponse configure(Dtos.ConfigurationRequest req) {
        if (req.productId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        Product product = productRepository.findByIdWithGroups(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.productId()));

        List<Long> selected = req.selectedOptionIds() == null ? List.of() : req.selectedOptionIds();
        Set<Long> selectedSet = new HashSet<>(selected);
        if (selectedSet.size() != selected.size()) {
            throw new IllegalArgumentException("Duplicate option selected");
        }

        Map<Long, ProductOption> optionById = product.getOptionGroups().stream()
                .flatMap(g -> g.getOptions().stream())
                .collect(Collectors.toMap(ProductOption::getId, o -> o));

        // all selected options must exist, be active and belong to product
        for (Long optionId : selectedSet) {
            ProductOption option = optionById.get(optionId);
            if (option == null || !option.isActive()) {
                throw new IllegalArgumentException("Invalid option: " + optionId);
            }
            if (option.getStock() <= 0) {
                throw new IllegalArgumentException("Option out of stock: " + optionId);
            }
        }

        // group count validation
        Map<Long, List<Long>> byGroup = new java.util.HashMap<>();
        for (OptionGroup group : product.getOptionGroups()) {
            List<Long> groupSelected = group.getOptions().stream()
                    .map(ProductOption::getId)
                    .filter(selectedSet::contains)
                    .toList();
            byGroup.put(group.getId(), groupSelected);
            if (group.isRequired() && groupSelected.size() < group.getMinSelections()) {
                throw new IllegalArgumentException("Missing required selection for group: " + group.getName());
            }
            if (groupSelected.size() > group.getMaxSelections()) {
                throw new IllegalArgumentException("Too many selections for group: " + group.getName());
            }
        }

        // conflicts
        for (Long sourceId : selectedSet) {
            for (ConflictRule rule : conflictRuleRepository.findBySourceOptionId(sourceId)) {
                boolean targetSelected = selectedSet.contains(rule.getTargetOption().getId());
                if ("EXCLUDES".equalsIgnoreCase(rule.getRuleType()) && targetSelected) {
                    throw new IllegalArgumentException("Incompatible options selected");
                }
                if ("REQUIRES".equalsIgnoreCase(rule.getRuleType()) && !targetSelected) {
                    throw new IllegalArgumentException("Required option missing for: " + sourceId);
                }
            }
        }

        BigDecimal total = product.getBasePrice();
        List<Dtos.PriceLine> breakdown = new ArrayList<>();
        breakdown.add(new Dtos.PriceLine(product.getName() + " (base)", product.getBasePrice()));
        for (Long optionId : selected) {
            ProductOption option = optionById.get(optionId);
            total = total.add(option.getPriceDelta());
            if (option.getPriceDelta().compareTo(BigDecimal.ZERO) != 0) {
                breakdown.add(new Dtos.PriceLine(option.getName(), option.getPriceDelta()));
            }
        }

        Configuration configuration = new Configuration();
        configuration.setProductId(product.getId());
        configuration.setSelectedOptionIds(new ArrayList<>(selected));
        configuration.setTotalPrice(total);
        configuration.setSessionId(req.sessionId());
        configurationRepository.save(configuration);

        return new Dtos.ConfigurationResponse(
                configuration.getId(), product.getId(), new java.util.ArrayList<>(configuration.getSelectedOptionIds()),
                total, configuration.getStatus(), breakdown);
    }

    @Transactional(readOnly = true)
    public Dtos.ConfigurationResponse get(String id) {
        Configuration c = configurationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found: " + id));
        return new Dtos.ConfigurationResponse(c.getId(), c.getProductId(), new java.util.ArrayList<>(c.getSelectedOptionIds()),
                c.getTotalPrice(), c.getStatus(), List.of());
    }
}
