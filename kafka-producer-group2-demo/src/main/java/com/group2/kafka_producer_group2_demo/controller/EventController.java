package com.group2.kafka_producer_group2_demo.controller;

import com.group2.kafka_producer_group2_demo.KafkaEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private KafkaEventConsumer consumer;

    @GetMapping
    public ResponseEntity<?> listEvents() {
        return ResponseEntity.ok(consumer.getRecentEvents());
    }
}
