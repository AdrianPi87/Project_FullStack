package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.Product;
import com.demo.configurator.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Dtos.ProductSummaryResponse> listActive() {
        return productRepository.findByActiveTrue().stream()
                .map(p -> new Dtos.ProductSummaryResponse(
                        p.getId(), p.getName(), p.getSlug(), p.getDescription(),
                        p.getCategory(), p.getImageUrl(), p.getBasePrice(), p.isActive()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Dtos.ProductDetailResponse getDetail(Long id) {
        Product p = productRepository.findByIdWithGroups(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return toDetail(p);
    }

    private Dtos.ProductDetailResponse toDetail(Product p) {
        List<Dtos.OptionGroupResponse> groups = p.getOptionGroups().stream()
                .map(g -> new Dtos.OptionGroupResponse(
                        g.getId(), g.getName(), g.getDisplayName(), g.getSelectionType(),
                        g.isRequired(), g.getMinSelections(), g.getMaxSelections(), g.getSortOrder(),
                        g.getOptions().stream()
                                .filter(o -> o.isActive())
                                .map(o -> new Dtos.OptionResponse(
                                        o.getId(), o.getName(), o.getDescription(), o.getSku(),
                                        o.getPriceDelta(), o.getStock(), o.getImageUrl(),
                                        o.isActive(), o.isDefaultOption(), o.getSortOrder()))
                                .toList()))
                .toList();
        return new Dtos.ProductDetailResponse(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(), p.getCategory(),
                p.getImageUrl(), p.getBasePrice(), p.isActive(), groups);
    }
}
