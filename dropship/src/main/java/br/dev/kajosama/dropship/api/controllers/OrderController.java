package br.dev.kajosama.dropship.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.OrderItemRequest;
import br.dev.kajosama.dropship.api.payloads.responses.OrderResponse;
import br.dev.kajosama.dropship.api.services.OrderService;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;

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

    @GetMapping("/shopping-cart")
    public ResponseEntity<OrderResponse> getUserShoppingCart() {
        return ResponseEntity.ok(
            OrderResponse.fromEntity(orderService.getUserShoppingCart())
        );
    }

    @PostMapping
    public ResponseEntity<OrderResponse> registerOrder(@RequestBody List<OrderItemRequest> items) {
        return ResponseEntity.ok(
            OrderResponse.fromEntity(orderService.registerOrder(items))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> updatePendingOrder(@PathVariable Long id, @RequestBody List<OrderItemRequest> items) {
        return ResponseEntity.ok(
            OrderResponse.fromEntity(orderService.updatePendingOrder(id, items))
        );
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}