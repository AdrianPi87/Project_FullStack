package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/events")
    public Dtos.AnalyticsEventResponse record(@RequestBody Dtos.AnalyticsEventRequest request) {
        return analyticsService.record(request);
    }

    @GetMapping("/events")
    public List<Dtos.AnalyticsEventResponse> recent() {
        return analyticsService.recent();
    }
}
