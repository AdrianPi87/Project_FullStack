package com.demo.configurator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dtos {
    private Dtos() {
    }

    public record OptionResponse(Long id, String name, String description, String sku,
                                BigDecimal priceDelta, int stock, String imageUrl,
                                boolean active, boolean defaultOption, int sortOrder) {
    }

    public record OptionGroupResponse(Long id, String name, String displayName, String selectionType,
                                     boolean required, int minSelections, int maxSelections,
                                     int sortOrder, List<OptionResponse> options) {
    }

    public record ProductSummaryResponse(Long id, String name, String slug, String description,
                                         String category, String imageUrl, BigDecimal basePrice,
                                         boolean active) {
    }

    public record ProductDetailResponse(Long id, String name, String slug, String description,
                                       String category, String imageUrl, BigDecimal basePrice,
                                       boolean active, List<OptionGroupResponse> optionGroups) {
    }

    public record ConfigurationRequest(@NotNull Long productId, List<Long> selectedOptionIds, String sessionId) {
    }

    public record PriceLine(String label, BigDecimal amount) {
    }

    public record ConfigurationResponse(String id, Long productId, List<Long> selectedOptionIds,
                                       BigDecimal totalPrice, String status,
                                       List<PriceLine> priceBreakdown) {
    }

    public record CartItemRequest(@NotBlank String sessionId, @NotNull Long productId, List<Long> selectedOptionIds,
                                 @Positive Integer quantity) {
    }

    public record CartItemResponse(String id, Long productId, String productName,
                                  List<Long> selectedOptionIds, int quantity,
                                  BigDecimal unitPrice, BigDecimal lineTotal) {
    }

    public record CartResponse(String sessionId, List<CartItemResponse> items, BigDecimal total) {
    }

    public record QuantityUpdateRequest(int quantity) {
    }

    public record CartMergeRequest(@NotBlank String sourceSessionId, @NotBlank String targetSessionId) {
    }

    public record OrderRequest(@NotBlank String sessionId) {
    }

    public record OrderItemResponse(String id, Long productId, String productName,
                                    List<Long> selectedOptionIds, int quantity,
                                    BigDecimal unitPrice, BigDecimal lineTotal) {
    }

    public record OrderResponse(String id, String sessionId, List<OrderItemResponse> items,
                               BigDecimal total, String status, LocalDateTime createdAt) {
    }

    public record AnalyticsEventRequest(String type, String entityId, String payload, String sessionId) {
    }

    public record AnalyticsEventResponse(String id, String type, String entityId, String payload,
                                        String sessionId, LocalDateTime createdAt) {
    }

    public record RegisterRequest(@NotBlank @Email String email, @NotBlank String password, String displayName) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record AuthResponse(String token, Long userId, String email, String role) {
    }

    public record UserResponse(Long id, String email, String displayName, String role, java.time.LocalDateTime createdAt) {
    }
}
