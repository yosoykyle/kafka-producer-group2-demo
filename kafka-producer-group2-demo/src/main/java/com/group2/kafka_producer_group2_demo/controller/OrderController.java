package com.group2.kafka_producer_group2_demo.controller;

import com.group2.kafka_producer_group2_demo.KafkaEventProducer;
import com.group2.kafka_producer_group2_demo.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private KafkaEventProducer producer;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto order) {
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "orderId is required"));
        }

        Map<String, Object> event = new HashMap<>();
        event.put("order_id", order.getOrderId());
        event.put("customer_name", order.getCustomerName());
        event.put("items", order.getItems());
        event.put("address", order.getAddress());
        event.put("timestamp", java.time.Instant.now().toString());

        producer.sendOrderPlacedEvent(event);

        return ResponseEntity.accepted().body(Map.of("status", "sent"));
    }
}
