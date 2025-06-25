package com.example.projeto_final_scd.inventory;

import com.example.projeto_final_scd.order.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {

    private static final String INVENTORY_EVENTS_TOPIC = "inventory-events";

    private final Map<String, Integer> inventoryStock = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public InventoryService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    private void initializeInventory() {
        inventoryStock.put("teclado-mecanico", 10);
        inventoryStock.put("mouse-gamer", 15);
        inventoryStock.put("headset-7.1", 5);
        inventoryStock.put("placa-de-video-rtx", 3);
        inventoryStock.put("memoria-ram-16gb", 0);
    }

    @KafkaListener(topics = "orders", groupId = "inventory-processing-group")
    public void consumeOrder(Order order) {
        System.out.println("INVENTORY-SERVICE: Consumed order -> " + order);

        boolean hasStock = checkStockForItems(order.getItems());

        String eventMessage;
        if (hasStock) {
            eventMessage = "SUCCESS: Inventory reserved for Order ID: " + order.getOrderId();
            System.out.println("INVENTORY-SERVICE: Stock available for order: " + order.getOrderId());
        } else {
            eventMessage = "FAILURE: Item out of stock or does not exist for Order ID: " + order.getOrderId();
            System.out.println("INVENTORY-SERVICE: Stock unavailable for order: " + order.getOrderId());
        }

        System.out.println("INVENTORY-SERVICE: Producing inventory event -> " + eventMessage);
        System.out.println();
        kafkaTemplate.send(INVENTORY_EVENTS_TOPIC, order.getOrderId(), eventMessage);
    }

    private boolean checkStockForItems(List<String> items) {
        for (String item : items) {
            int stock = inventoryStock.getOrDefault(item, 0);
            if (stock <= 0) {
                System.out.println("INVENTORY-SERVICE: Stock check FAILED for item: " + item + " (Quantity: " + stock + ")");
                return false;
            }
        }
        System.out.println("INVENTORY-SERVICE: Stock check PASSED for all items.");
        return true;
    }
}