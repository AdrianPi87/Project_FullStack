package com.demo.configurator.controller;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.service.ConfigurationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configurations")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping
    public Dtos.ConfigurationResponse create(@Valid @RequestBody Dtos.ConfigurationRequest request) {
        return configurationService.configure(request);
    }

    @GetMapping("/{id}")
    public Dtos.ConfigurationResponse get(@PathVariable String id) {
        return configurationService.get(id);
    }
}
