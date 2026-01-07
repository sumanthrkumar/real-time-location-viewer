package com.atlas.analytics.analytics_service.dto;

import java.time.Instant;

public record LocationUpdateDto(
                String deviceId,
                double latitude,
                double longitude,
                Instant timestamp) {
}
