package com.atlas.analytics.analytics_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.atlas.analytics.analytics_service.dto.LocationUpdateDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocationConsumer {
    @KafkaListener(topics = "location-updates", groupId = "analytics-group")
    public void consume(LocationUpdateDto locationUpdate) {
        log.info("Consumed message from deviceId {} with lat {} and lon {}", locationUpdate.deviceId(),
                locationUpdate.latitude(), locationUpdate.longitude());

        // Save message to REDIS

    }
}
