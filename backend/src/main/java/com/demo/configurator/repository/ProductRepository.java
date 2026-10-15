package com.demo.configurator.repository;

import com.demo.configurator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    Optional<Product> findBySlug(String slug);

    @Query("select p from Product p left join fetch p.optionGroups g left join fetch g.options where p.id = :id")
    Optional<Product> findByIdWithGroups(Long id);
}
