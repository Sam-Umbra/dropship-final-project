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

/**
 * REST controller for managing customer orders and shopping carts. Provides
 * endpoints for creating, retrieving, updating, and deleting orders.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    /**
     * Service for handling order and shopping cart business logic.
     */
    @Autowired
    private OrderService orderService;

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @return A {@link ResponseEntity} containing a list of
     * {@link OrderResponse} objects.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllUserOrders() {
        List<OrderResponse> orders = orderService.getAllUserOrders().stream()
                .map(OrderResponse::fromEntity).toList();

        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves the current shopping cart for the authenticated user. A
     * shopping cart is an order with a 'PENDING' status.
     *
     * @return A {@link ResponseEntity} containing the user's shopping cart as
     * an {@link OrderResponse}.
     */
    @GetMapping("/shopping-cart")
    public ResponseEntity<OrderResponse> getUserShoppingCart() {
        return ResponseEntity.ok(
                OrderResponse.fromEntity(orderService.getUserShoppingCart())
        );
    }

    /**
     * Registers a new order with the provided items. If a pending order
     * (shopping cart) exists, it will be updated. Otherwise, a new one is
     * created.
     *
     * @param items A list of {@link OrderItemRequest} objects representing the
     * items in the order.
     * @return A {@link ResponseEntity} containing the created or updated order
     * as an {@link OrderResponse}.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> registerOrder(@RequestBody List<OrderItemRequest> items) {
        return ResponseEntity.ok(
                OrderResponse.fromEntity(orderService.registerOrder(items))
        );
    }

    /**
     * Deletes an order by its ID. This is typically used to cancel an order.
     *
     * @param id The ID of the order to delete.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the items in a pending order (shopping cart).
     *
     * @param id The ID of the pending order to update.
     * @param items A list of {@link OrderItemRequest} objects with the updated
     * items.
     * @return A {@link ResponseEntity} containing the updated order as an
     * {@link OrderResponse}.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> updatePendingOrder(@PathVariable Long id, @RequestBody List<OrderItemRequest> items) {
        return ResponseEntity.ok(
                OrderResponse.fromEntity(orderService.updatePendingOrder(id, items))
        );
    }

    /**
     * Updates the status of an existing order.
     *
     * @param id The ID of the order to update.
     * @param status The new {@link OrderStatus} for the order.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @PatchMapping("/status/{id}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
