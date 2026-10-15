package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.Order;
import com.demo.configurator.model.OrderItem;
import com.demo.configurator.model.Product;
import com.demo.configurator.model.ProductOption;
import com.demo.configurator.repository.OrderRepository;
import com.demo.configurator.repository.ProductOptionRepository;
import com.demo.configurator.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;


    public OrderService(OrderRepository orderRepository, CartService cartService,
                        com.demo.configurator.repository.ProductRepository productRepository,
                        ProductOptionRepository productOptionRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @Transactional
    public Dtos.OrderResponse checkout(String sessionId) {
        Dtos.CartResponse cart = cartService.getCart(sessionId);
        if (cart.items().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        Order order = new Order();
        order.setSessionId(sessionId);
        BigDecimal total = BigDecimal.ZERO;
        List<Dtos.OrderItemResponse> itemResponses = new ArrayList<>();
        for (Dtos.CartItemResponse cartItem : cart.items()) {
            for (Long optionId : cartItem.selectedOptionIds()) {
                ProductOption option = productOptionRepository.findByIdForUpdate(optionId)
                        .orElseThrow(() -> new IllegalArgumentException("Option not found: " + optionId));
                if (option.getStock() < cartItem.quantity()) {
                    throw new IllegalArgumentException("Insufficient stock for option: " + optionId);
                }
                option.setStock(option.getStock() - cartItem.quantity());
                productOptionRepository.save(option);
            }
            OrderItem item = new OrderItem();
            item.setId(UUID.randomUUID().toString());
            item.setOrder(order);
            item.setProductId(cartItem.productId());
            item.setSelectedOptionIds(new ArrayList<>(cartItem.selectedOptionIds()));
            item.setQuantity(cartItem.quantity());
            item.setUnitPrice(cartItem.unitPrice());
            order.getItems().add(item);
            BigDecimal line = cartItem.unitPrice().multiply(BigDecimal.valueOf(cartItem.quantity()));
            total = total.add(line);
            Product product = productRepository.findById(cartItem.productId()).orElse(null);
            String productName = product == null ? "Unknown product" : product.getName();
            itemResponses.add(new Dtos.OrderItemResponse(item.getId(), cartItem.productId(), productName,
                    cartItem.selectedOptionIds(), cartItem.quantity(), cartItem.unitPrice(), line));
        }
        order.setTotal(total);
        orderRepository.save(order);
        cartService.clear(sessionId);
        return new Dtos.OrderResponse(order.getId(), sessionId, itemResponses, total, order.getStatus(), order.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<Dtos.OrderResponse> list(String sessionId) {
        return orderRepository.findBySessionIdOrderByCreatedAtDesc(sessionId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Dtos.OrderResponse get(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return toResponse(order);
    }

    private Dtos.OrderResponse toResponse(Order order) {
        List<Dtos.OrderItemResponse> items = order.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            String productName = product == null ? "Unknown product" : product.getName();
            BigDecimal line = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new Dtos.OrderItemResponse(item.getId(), item.getProductId(), productName,
                    new java.util.ArrayList<>(item.getSelectedOptionIds()), item.getQuantity(), item.getUnitPrice(), line);
        }).toList();
        BigDecimal total = items.stream().map(Dtos.OrderItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Dtos.OrderResponse(order.getId(), order.getSessionId(), items, total, order.getStatus(), order.getCreatedAt());
    }
}
