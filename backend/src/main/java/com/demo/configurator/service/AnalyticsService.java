package com.demo.configurator.service;

import com.demo.configurator.dto.Dtos;
import com.demo.configurator.model.AnalyticsEvent;
import com.demo.configurator.repository.AnalyticsEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsEventRepository repository;

    public AnalyticsService(AnalyticsEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Dtos.AnalyticsEventResponse record(Dtos.AnalyticsEventRequest req) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setType(req.type());
        event.setEntityId(req.entityId());
        event.setPayload(req.payload());
        event.setSessionId(req.sessionId());
        repository.save(event);
        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<Dtos.AnalyticsEventResponse> recent() {
        return repository.findTop50ByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    private Dtos.AnalyticsEventResponse toResponse(AnalyticsEvent event) {
        return new Dtos.AnalyticsEventResponse(event.getId(), event.getType(), event.getEntityId(),
                event.getPayload(), event.getSessionId(), event.getCreatedAt());
    }
}
