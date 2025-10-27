package com.group2.kafka_producer_group2_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderPlacedEvent() {
        // Deprecated: sample send on startup removed. Use sendOrderPlacedEvent(Map) from controllers or other callsites.
    }

    // Accept a DTO/map and send it to Kafka
    public void sendOrderPlacedEvent(Map<String, Object> event) {
        kafkaTemplate.send("OrderPlaced", event);
    }
}