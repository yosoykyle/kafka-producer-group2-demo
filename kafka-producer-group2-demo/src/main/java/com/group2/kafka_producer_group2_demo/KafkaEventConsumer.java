package com.group2.kafka_producer_group2_demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.ArrayList;
import java.util.Deque;

@Component
public class KafkaEventConsumer {

    // Keep last 100 events in-memory for the UI API
    private final Deque<Map<String, Object>> recentEvents = new LinkedBlockingDeque<>(100);

    @KafkaListener(topics = "OrderPlaced", groupId = "notification-group")
    public void consumeOrderPlacedEvent(Map<String, Object> event) {
        System.out.println("\n $_$ Notification Service received event:");
        System.out.println("   Order ID: " + event.get("order_id"));
        System.out.println("   Customer: " + event.get("customer_name"));

        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) event.get("items");
        System.out.println("   Items: " + items); // Just print the list directly!

        System.out.println("   Address: " + event.get("address"));
        System.out.println("   Timestamp: " + event.get("timestamp"));

        // store event (thread-safe deque)
        synchronized (recentEvents) {
            if (recentEvents.size() == 100) {
                recentEvents.removeFirst();
            }
            recentEvents.addLast(event);
        }

        // Simulate sending SMS/email
        System.out.println(" Sent notification to customer: 'Your order is confirmed! \u2606*: .\uff61. o(\u2267\u25bd\u2266)o .\uff61.:*\u2606'");
    }

    public List<Map<String, Object>> getRecentEvents() {
        synchronized (recentEvents) {
            return new ArrayList<>(recentEvents);
        }
    }
}