package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String sessionId(String requested, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return requested;
    }

    @PostMapping
    public Dtos.OrderResponse checkout(@Valid @RequestBody Dtos.OrderRequest request, Authentication auth) {
        return orderService.checkout(sessionId(request.sessionId(), auth));
    }

    @GetMapping
    public List<Dtos.OrderResponse> list(@RequestParam String sessionId, Authentication auth) {
        return orderService.list(sessionId(sessionId, auth));
    }

    @GetMapping("/{id}")
    public Dtos.OrderResponse get(@PathVariable String id) {
        return orderService.get(id);
    }
}
