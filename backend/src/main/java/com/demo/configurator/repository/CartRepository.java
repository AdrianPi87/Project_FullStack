package com.demo.configurator.repository;

import com.demo.configurator.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findBySessionId(String sessionId);
}
