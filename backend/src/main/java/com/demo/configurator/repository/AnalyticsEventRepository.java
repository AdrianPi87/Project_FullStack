package com.demo.configurator.repository;

import com.demo.configurator.model.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, String> {
    List<AnalyticsEvent> findTop50ByOrderByCreatedAtDesc();
}
