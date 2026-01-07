package com.atlas.ingestion.controller;

import com.atlas.ingestion.dto.LocationUpdateDto;
import com.atlas.ingestion.service.IngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public ResponseEntity<Void> updateLocation(@RequestBody LocationUpdateDto locationDto) {
        ingestionService.processLocation(locationDto);

        return ResponseEntity.accepted().build();
    }
}
