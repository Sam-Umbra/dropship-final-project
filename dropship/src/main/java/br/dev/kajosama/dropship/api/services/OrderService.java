package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.exceptions.EntityAlreadyExistsException;
import br.dev.kajosama.dropship.api.payloads.requests.OrderItemRequest;
import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.entities.OrderItem;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;
import br.dev.kajosama.dropship.domain.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    public Order saveOrder(Order o) {
        return orderRepository.save(o);
    }

    public List<Order> getAllUserOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        if (orderRepository.findAllByUserId(currentUser.getId()).isEmpty()) {
            throw new EntityNotFoundException("Orders of user: " + currentUser.getName() + "NOT FOUND");
        }

        return orderRepository.findAllByUserId(currentUser.getId());
    }

    public Order getUserShoppingCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        return orderRepository.findByUserIdAndStatus(currentUser.getId(), OrderStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user: " + currentUser.getName()));
    }

    public Order registerOrder(List<OrderItemRequest> items) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        if (userHasPendingOrder(currentUser.getId())) {
            throw new EntityAlreadyExistsException("Order", "status", "PENDING");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("The order item list must not be null");
        }

        Order order = new Order(currentUser, OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        for (OrderItemRequest request : items) {
            Product product = productService.getProductById(request.productId());

            if (request.quantity() == null || request.quantity() <= 0) {
                throw new IllegalArgumentException("Ilegal quantity for the product " + product.getName());
            }

            OrderItem orderItem = new OrderItem(product, request.quantity());
            order.addItem(orderItem);
        }

        return saveOrder(order);
    }

    public void deleteOrder(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (!orderUserMatchesCurrentUser(order)) {
            throw new AccessDeniedException("You can't delete an order that doesn't belong to you unless you are an ADMIN");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Only orders with PENDING or CANCELLED status can be deleted");
        }

        orderRepository.delete(order);
    }

    public Order updatePendingOrder(Long id, List<OrderItemRequest> items) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (!orderUserMatchesCurrentUser(order)) {
            throw new AccessDeniedException("You can't modify an order that doesn't belong to you unless you are an ADMIN");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be edited");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("The order item list must not be null or empty");
        }

        List<Long> incomingProductIds = items.stream()
                .map(OrderItemRequest::productId)
                .toList();

        order.getItems().removeIf(item
                -> !incomingProductIds.contains(item.getProduct().getId())
        );

        for (OrderItemRequest request : items) {
            Product product = productService.getProductById(request.productId());

            if (request.quantity() == null || request.quantity() <= 0) {
                throw new IllegalArgumentException("Illegal quantity for product " + product.getName());
            }

            OrderItem existingItem = order.getItems().stream()
                    .filter(it -> it.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(request.quantity());
            } else {
                OrderItem newItem = new OrderItem(product, request.quantity());
                order.addItem(newItem);
            }
        }

        return saveOrder(order);
    }

    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    public boolean orderUserMatchesCurrentUser(Order order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        return order.getUser().getId().equals(currentUser.getId()) || currentUser.hasRole("ADMIN");
    }

    public boolean userHasPendingOrder(Long userId) {
        return orderRepository.existsByUserIdAndStatus(userId, OrderStatus.PENDING);
    }
}
