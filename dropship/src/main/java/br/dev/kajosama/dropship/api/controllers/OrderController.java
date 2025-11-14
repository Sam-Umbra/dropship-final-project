package br.dev.kajosama.dropship.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.OrderItemRequest;
import br.dev.kajosama.dropship.api.payloads.responses.OrderResponse;
import br.dev.kajosama.dropship.api.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllUserOrders() {
        List<OrderResponse> orders = orderService.getAllUserOrders().stream()
            .map(OrderResponse::fromEntity).toList();

        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> registerOrder(@RequestBody List<OrderItemRequest> items) {
        return ResponseEntity.ok(
            OrderResponse.fromEntity(orderService.registerOrder(items))
        );
    }

}