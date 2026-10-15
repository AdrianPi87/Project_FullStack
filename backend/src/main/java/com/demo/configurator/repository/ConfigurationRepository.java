package com.demo.configurator.repository;

import com.demo.configurator.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
    Optional<Configuration> findByIdAndSessionId(String id, String sessionId);
}
