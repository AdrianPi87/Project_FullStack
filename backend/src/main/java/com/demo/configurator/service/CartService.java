package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.Cart;
import com.demo.configurator.model.CartItem;
import com.demo.configurator.model.Product;
import com.demo.configurator.model.ProductOption;
import com.demo.configurator.repository.CartRepository;
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
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Dtos.CartResponse getCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId).orElse(null);
        if (cart == null) {
            return new Dtos.CartResponse(sessionId, List.of(), BigDecimal.ZERO);
        }
        return toResponse(cart);
    }

    @Transactional
    public Dtos.CartResponse addItem(Dtos.CartItemRequest req) {
        if (req.sessionId() == null || req.sessionId().isBlank()) {
            throw new IllegalArgumentException("sessionId is required");
        }
        if (req.productId() == null) {
            throw new IllegalArgumentException("productId is required");
        }
        Product product = productRepository.findByIdWithGroups(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + req.productId()));

        List<Long> selected = req.selectedOptionIds() == null ? List.of() : req.selectedOptionIds();
        Set<Long> selectedSet = new HashSet<>(selected);
        Map<Long, ProductOption> optionById = product.getOptionGroups().stream()
                .flatMap(g -> g.getOptions().stream())
                .collect(Collectors.toMap(ProductOption::getId, o -> o));
        for (Long id : selectedSet) {
            ProductOption option = optionById.get(id);
            if (option == null || !option.isActive()) {
                throw new IllegalArgumentException("Invalid option: " + id);
            }
        }

        int quantity = req.quantity() == null || req.quantity() < 1 ? 1 : req.quantity();
        for (Long id : selectedSet) {
            if (optionById.get(id).getStock() < quantity) {
                throw new IllegalArgumentException("Insufficient stock for option: " + id);
            }
        }
        BigDecimal unitPrice = product.getBasePrice();
        for (Long id : selected) {
            unitPrice = unitPrice.add(optionById.get(id).getPriceDelta());
        }

        Cart cart = cartRepository.findBySessionId(req.sessionId()).orElseGet(() -> new Cart(req.sessionId()));

        // merge if same product + same selected options
        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(req.productId()) && new HashSet<>(i.getSelectedOptionIds()).equals(selectedSet))
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(req.productId());
            item.setSelectedOptionIds(new ArrayList<>(selected));
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public Dtos.CartResponse updateQuantity(String sessionId, String itemId, int quantity) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (quantity < 1) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public Dtos.CartResponse removeItem(String sessionId, String itemId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public void clear(String sessionId) {
        cartRepository.findBySessionId(sessionId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    @Transactional
    public Dtos.CartResponse mergeCarts(String sourceSessionId, String targetSessionId) {
        if (sourceSessionId == null || sourceSessionId.isBlank() || targetSessionId == null || targetSessionId.isBlank()
                || sourceSessionId.equals(targetSessionId)) {
            return getCart(targetSessionId);
        }
        Cart source = cartRepository.findBySessionId(sourceSessionId).orElse(null);
        if (source == null || source.getItems().isEmpty()) {
            return getCart(targetSessionId);
        }
        Cart target = cartRepository.findBySessionId(targetSessionId).orElseGet(() -> new Cart(targetSessionId));
        for (CartItem item : new ArrayList<>(source.getItems())) {
            CartItem existing = target.getItems().stream()
                    .filter(i -> i.getProductId().equals(item.getProductId())
                            && new HashSet<>(i.getSelectedOptionIds()).equals(new HashSet<>(item.getSelectedOptionIds())))
                    .findFirst().orElse(null);
            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                CartItem copy = new CartItem();
                copy.setCart(target);
                copy.setProductId(item.getProductId());
                copy.setSelectedOptionIds(new ArrayList<>(item.getSelectedOptionIds()));
                copy.setQuantity(item.getQuantity());
                copy.setUnitPrice(item.getUnitPrice());
                target.getItems().add(copy);
            }
        }
        source.getItems().clear();
        cartRepository.save(source);
        cartRepository.save(target);
        return toResponse(target);
    }
    private Dtos.CartResponse toResponse(Cart cart) {
        List<Dtos.CartItemResponse> items = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            String productName = product == null ? "Unknown product" : product.getName();
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new Dtos.CartItemResponse(item.getId(), item.getProductId(), productName,
                    new java.util.ArrayList<>(item.getSelectedOptionIds()), item.getQuantity(), item.getUnitPrice(), lineTotal);
        }).toList();
        BigDecimal total = items.stream()
                .map(Dtos.CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dtos.CartResponse(cart.getSessionId(), items, total);
    }
}
