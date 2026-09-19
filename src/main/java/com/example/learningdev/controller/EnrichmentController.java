package com.example.learningdev.controller;

import com.example.learningdev.dto.EnrichRequest;
import com.example.learningdev.model.EnrichedPost;
import com.example.learningdev.service.EnrichmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing endpoints to enrich and persist external data.
 * POST /api/enrich accepts a JSON payload with a location name and returns the persisted entity.
 */
@RestController
@RequestMapping("/api")
public class EnrichmentController {

    private final EnrichmentService service;
    private final com.example.learningdev.repository.EnrichedPostRepository repository;

    public EnrichmentController(EnrichmentService service, com.example.learningdev.repository.EnrichedPostRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/enrich")
    public ResponseEntity<EnrichedPost> enrich(@RequestBody EnrichRequest request) {
        EnrichedPost saved = service.enrichAndSave(request.location());
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/enriched")
    public ResponseEntity<java.util.List<EnrichedPost>> listAll() {
        java.util.List<EnrichedPost> all = repository.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/enriched/sorted")
    public ResponseEntity<java.util.List<EnrichedPost>> listByTemperatureDesc() {
        java.util.List<EnrichedPost> sorted = repository.findAllByOrderByTemperatureDesc();
        return ResponseEntity.ok(sorted);
    }
}
