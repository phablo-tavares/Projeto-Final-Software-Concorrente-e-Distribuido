package com.example.projeto_final_scd.order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public String createOrder(@RequestBody List<String> items) {
        Order newOrder = new Order(items);
        orderService.sendOrder(newOrder);
        return "Order received and sent for processing. Order ID: " + newOrder.getOrderId();
    }
}