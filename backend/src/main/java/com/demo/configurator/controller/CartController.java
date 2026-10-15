package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String sessionId(String requested, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return requested;
    }

    @GetMapping
    public Dtos.CartResponse get(@RequestParam String sessionId, Authentication auth) {
        return cartService.getCart(sessionId(sessionId, auth));
    }

    @PostMapping("/items")
    public Dtos.CartResponse add(@Valid @RequestBody Dtos.CartItemRequest request, Authentication auth) {
        Dtos.CartItemRequest resolved = new Dtos.CartItemRequest(sessionId(request.sessionId(), auth),
                request.productId(), request.selectedOptionIds(), request.quantity());
        return cartService.addItem(resolved);
    }

    @PatchMapping("/items/{itemId}")
    public Dtos.CartResponse update(@RequestParam String sessionId,
                                   @PathVariable String itemId,
                                   @Valid @RequestBody Dtos.QuantityUpdateRequest request,
                                   Authentication auth) {
        return cartService.updateQuantity(sessionId(sessionId, auth), itemId, request.quantity());
    }

    @PostMapping("/merge")
    public Dtos.CartResponse merge(@Valid @RequestBody Dtos.CartMergeRequest request, Authentication auth) {
        String target = sessionId(request.targetSessionId(), auth);
        return cartService.mergeCarts(request.sourceSessionId(), target);
    }

    @DeleteMapping("/items/{itemId}")
    public Dtos.CartResponse remove(@RequestParam String sessionId, @PathVariable String itemId, Authentication auth) {
        return cartService.removeItem(sessionId(sessionId, auth), itemId);
    }
}
