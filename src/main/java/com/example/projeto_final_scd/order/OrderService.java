package com.example.projeto_final_scd.order;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final String TOPIC_NAME = "orders";

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderService(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(Order order) {
        System.out.println("PRODUCER: Producing order message -> " + order);
        this.kafkaTemplate.send(TOPIC_NAME, order.getOrderId(), order);
    }
}