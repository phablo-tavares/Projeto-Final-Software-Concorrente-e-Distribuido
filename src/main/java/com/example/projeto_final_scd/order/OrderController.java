package com.example.projeto_final_scd.order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducerService producerService;

    public OrderController(OrderProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public String createOrder(@RequestBody List<String> items) {
        Order newOrder = new Order(items);
        producerService.sendOrder(newOrder);
        return "Order received and sent for processing. Order ID: " + newOrder.getOrderId();
    }
}