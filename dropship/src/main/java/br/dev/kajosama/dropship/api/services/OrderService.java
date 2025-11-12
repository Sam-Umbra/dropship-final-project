package br.dev.kajosama.dropship.api.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.payloads.requests.OrderItemRequest;
import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.entities.OrderItem;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;
import br.dev.kajosama.dropship.domain.repositories.OrderRepository;


@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    public Order registerOrder(List<OrderItemRequest> items) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
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

        return orderRepository.save(order); 
    }
}