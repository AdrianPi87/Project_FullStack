package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Dtos.ProductSummaryResponse> list() {
        return productService.listActive();
    }

    @GetMapping("/{id}")
    public Dtos.ProductDetailResponse get(@PathVariable Long id) {
        return productService.getDetail(id);
    }
}
