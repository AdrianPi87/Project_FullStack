package com.demo.configurator.repository;

import com.demo.configurator.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findBySessionIdOrderByCreatedAtDesc(String sessionId);
}
