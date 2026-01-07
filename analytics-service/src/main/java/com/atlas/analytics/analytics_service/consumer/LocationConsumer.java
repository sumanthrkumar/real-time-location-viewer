package com.atlas.analytics.analytics_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import com.atlas.analytics.analytics_service.dto.LocationUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationConsumer {

    private final RedisTemplate<String, LocationUpdateDto> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final double SPEED_LIMIT = 100.0;

    @KafkaListener(topics = "location-updates")
    public void consume(LocationUpdateDto currentLocationUpdate) {
        log.info("Received from {} coordinates {},{}", currentLocationUpdate.deviceId(), 
                currentLocationUpdate.latitude(), currentLocationUpdate.longitude());

        String key = currentLocationUpdate.deviceId();

        // Fetch any previous record from Redis
        LocationUpdateDto previousLocationUpdate = redisTemplate.opsForValue().get(key);

        // If previous record exists else create new record and set totalDistance to 0.0
        if (previousLocationUpdate != null) {

            // Calculate distance using Haversine distance formula
            double distance = calculateDistance(previousLocationUpdate.latitude(), previousLocationUpdate.longitude(), currentLocationUpdate.latitude(), currentLocationUpdate.longitude());
            log.info("Distance travled by {} is {}km", currentLocationUpdate.deviceId(), distance);

            double newTotalDistance = previousLocationUpdate.totalDistance() + distance;
            currentLocationUpdate = new LocationUpdateDto(
                currentLocationUpdate.deviceId(), 
                currentLocationUpdate.latitude(), 
                currentLocationUpdate.longitude(),
                currentLocationUpdate.timestamp(),
                newTotalDistance
            );

            // Calculate duration
            double duration = Duration.between(previousLocationUpdate.timestamp(), currentLocationUpdate.timestamp()).toMillis();

            // Calculate speed, dividing by 3600.0 to convert km/sec to km/h
            if (duration > 0) {
                duration = duration / 1000.0;
                double speed = distance / (duration / 3600.0);

                if (speed > SPEED_LIMIT) {
                    log.warn("ALERT: Device {} is SPEEDING! ({} km/h)", 
                        currentLocationUpdate.deviceId(), String.format("%.2f", speed));
                }
                
                log.info("Distance travled by {} is {}km with speed {}km/h", currentLocationUpdate.deviceId(), distance, speed);
            }
            
        } else {
            currentLocationUpdate = new LocationUpdateDto(
                currentLocationUpdate.deviceId(), 
                currentLocationUpdate.latitude(), 
                currentLocationUpdate.longitude(),
                currentLocationUpdate.timestamp(),
                0.0
            );
        }

        // Save current update to Redis
        redisTemplate.opsForValue().set(key, currentLocationUpdate);

        //Publish message
        messagingTemplate.convertAndSend("/topic/location-updates", currentLocationUpdate);
    }

    private double calculateDistance(double prevLat, double prevLong, double currLat, double currLong) {
        prevLat = Math.toRadians(prevLat);
        prevLong = Math.toRadians(prevLong);
        currLat = Math.toRadians(currLat);
        currLong = Math.toRadians(currLong);

        double deltaLat = currLat - prevLat;
        double deltaLong = currLong - prevLong;

        double stepOne = Math.pow((Math.sin(deltaLat / 2)), 2);
        double stepTwo = (Math.cos(prevLat) * Math.cos(currLat)) * Math.pow(Math.sin(deltaLong / 2), 2);

        double relativeSquaredDist = stepOne + stepTwo;
        double angularDistance = 2 * Math.atan2(Math.sqrt(relativeSquaredDist), Math.sqrt(1 - relativeSquaredDist));

        return angularDistance * 6371;
    }
}
