package com.example.projeto_final_scd.notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(topics = "inventory-events", groupId = "notification-dispatch-group")
    public void consumeInventoryEvent(String message) {
        System.out.println("--- NOTIFICATION SERVICE ---");
        System.out.println("Received inventory event.");
        System.out.println("EVENT: [ " + message + " ]");
        System.out.println("--- NOTIFICATION 'SENT' SUCCESSFULLY ---");
        System.out.println();
        System.out.println();
    }
}