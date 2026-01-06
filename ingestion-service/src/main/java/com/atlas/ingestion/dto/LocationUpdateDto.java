package com.atlas.ingestion.dto;

import java.time.Instant;

public record LocationUpdate(
    String deviceId,
    double latitude,
    double longitude,
    Instant timestamp
) {}
