package com.atlas.ingestion.service;

import com.atlas.ingestion.dto.LocationUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class IngestionService {

    private static final Logger logger = LoggerFactory.getLogger(IngestionService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String topic;

    public IngestionService(KafkaTemplate<String, Object> kafkaTemplate, @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }


    public void processLocation(LocationUpdateDto location) {
        // logger.info("Received {}, at {}, {}", location.deviceId(), location.latitude(), location.longitude());
        if (location == null || location.deviceId() == null) {
            logger.error("Rejecting update. location or location.deviceId is null");
            return;
        }

        // Send a message to kafka with key being the deviceId
        kafkaTemplate.send(topic, location.deviceId(), location);

        logger.info("Produced message to kafka for device {}", location.deviceId());
    }

}