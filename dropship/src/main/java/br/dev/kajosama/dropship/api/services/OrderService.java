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

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Order} entities.
 *              Provides business logic for order-related operations such as
 *              retrieving, registering, updating, and deleting orders, as well
 *              as
 *              managing the user's shopping cart. It interacts with
 *              {@link OrderRepository} and {@link ProductService}.
 */
@Service
@Transactional
public class OrderService {

    /**
     * Repository for {@link Order} entities.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Service for {@link Product} entities.
     */
    @Autowired
    private ProductService productService;

    /**
     * Saves an {@link Order} entity to the database.
     *
     * @param o The {@link Order} entity to save.
     * @return The saved {@link Order} entity.
     */
    public Order saveOrder(Order o) {
        return orderRepository.save(o);
    }

    /**
     * Retrieves a list of all orders placed by the currently authenticated user.
     *
     * @return A {@link List} of {@link Order} entities belonging to the current
     *         user.
     * @throws AccessDeniedException   If the current user is not found or the token
     *                                 is invalid.
     * @throws EntityNotFoundException If no orders are found for the current user.
     */
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

    /**
     * Retrieves the shopping cart (an order with PENDING status) for the currently
     * authenticated user.
     *
     * @return The {@link Order} entity representing the user's shopping cart.
     * @throws AccessDeniedException   If the current user is not found or the token
     *                                 is invalid.
     * @throws EntityNotFoundException If no shopping cart (PENDING order) is found
     *                                 for the current user.
     * @see OrderStatus#PENDING
     */
    public Order getUserShoppingCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        return orderRepository.findByUserIdAndStatus(currentUser.getId(), OrderStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user: " + currentUser.getName()));
    }

    /**
     * Registers a new order for the currently authenticated user with the specified
     * items.
     * The order is initially created with {@link OrderStatus#PENDING}.
     *
     * @param items A {@link List} of {@link OrderItemRequest} objects detailing the
     *              products and quantities for the order.
     * @return The newly registered {@link Order} entity.
     * @throws AccessDeniedException        If the current user is not found or the
     *                                      token is invalid.
     * @throws EntityAlreadyExistsException If the user already has a PENDING order
     *                                      (shopping cart).
     * @throws IllegalArgumentException     If the item list is null or empty, or if
     *                                      any product quantity is invalid.
     * @throws EntityNotFoundException      If any product specified in the items is
     *                                      not found.
     */
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

    /**
     * Deletes an order by its ID.
     * Only the owner of the order or an ADMIN can delete an order.
     * Only orders with {@link OrderStatus#PENDING} or {@link OrderStatus#CANCELLED}
     * can be deleted.
     *
     * @param id The ID of the order to delete.
     * @throws EntityNotFoundException If the order with the given ID is not found.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to delete the order.
     * @throws IllegalStateException   If the order's status is not PENDING or
     *                                 CANCELLED.
     */
    public void deleteOrder(Long id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (!orderUserMatchesCurrentUser(order)) {
            throw new AccessDeniedException(
                    "You can't delete an order that doesn't belong to you unless you are an ADMIN");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Only orders with PENDING or CANCELLED status can be deleted");
        }

        orderRepository.delete(order);
    }

    /**
     * Updates a pending order with a new list of items.
     * Existing items in the order are updated or removed, and new items are added
     * based on the provided list.
     * Only the owner of the order or an ADMIN can modify it, and only if its status
     * is {@link OrderStatus#PENDING}.
     *
     * @param id    The ID of the order to update.
     * @param items A {@link List} of {@link OrderItemRequest} objects representing
     *              the desired state of the order items.
     * @return The updated {@link Order} entity.
     * @throws EntityNotFoundException  If the order with the given ID is not found.
     * @throws AccessDeniedException    If the current user does not have permission
     *                                  to modify the order.
     * @throws IllegalStateException    If the order's status is not PENDING.
     * @throws IllegalArgumentException If the item list is null or empty, or if any
     *                                  product quantity is invalid.
     */
    public Order updatePendingOrder(Long id, List<OrderItemRequest> items) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (!orderUserMatchesCurrentUser(order)) {
            throw new AccessDeniedException(
                    "You can't modify an order that doesn't belong to you unless you are an ADMIN");
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

        order.getItems().removeIf(item -> !incomingProductIds.contains(item.getProduct().getId()));

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

    /**
     * Updates the status of an order.
     * The status transition logic is enforced: for example, a PENDING order can
     * only become PAID or CANCELLED.
     * Only the owner of the order or an ADMIN can change its status.
     *
     * @param id        The ID of the order to update.
     * @param newStatus The new {@link OrderStatus} to set for the order.
     * @throws EntityNotFoundException If the order with the given ID is not found.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to modify the order.
     * @throws IllegalStateException   If the requested status transition is invalid
     *                                 for the current order status,
     *                                 or if the order is already in a final state
     *                                 (DELIVERED, CANCELLED, REFUNDED).
     */
    public void updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id " + id + " not found"));

        if (!orderUserMatchesCurrentUser(order)) {
            throw new AccessDeniedException(
                    "You can't modify an order that doesn't belong to you unless you are an ADMIN");
        }

        OrderStatus currentStatus = order.getStatus();

        switch (currentStatus) {
            case PENDING -> {
                if (newStatus != OrderStatus.PAID && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("A PENDING order can only be changed to PAID or CANCELLED.");
                }
            }
            case PAID -> {
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.REFUNDED
                        && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException(
                            "A PAID order can only be changed to PROCESSING, REFUNDED or CANCELLED.");
                }
            }
            case PROCESSING -> {
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("A PROCESSING order can only be changed to SHIPPED or CANCELLED.");
                }
            }
            case SHIPPED -> {
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("A SHIPPED order can only be changed to DELIVERED.");
                }
            }
            case DELIVERED, CANCELLED, REFUNDED -> throw new IllegalStateException(
                    "Cannot change status of an order that is already " + currentStatus + ".");
        }

        order.setStatus(newStatus);
        saveOrder(order);
    }

    /**
     * Checks if an order with the given ID exists.
     *
     * @param id The ID of the order to check.
     * @return True if an order with the specified ID exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    /**
     * Checks if the currently authenticated user is the owner of the given order or
     * has an ADMIN role.
     * This method is used for authorization checks before performing operations on
     * an order.
     *
     * @param order The {@link Order} entity to check ownership against.
     * @return True if the current user is the order's owner or an ADMIN, false
     *         otherwise.
     * @throws AccessDeniedException If the current user is not found or the token
     *                               is invalid.
     * @see User#hasRole(String)
     * @see SecurityContextHolder#getContext()
     */
    public boolean orderUserMatchesCurrentUser(Order order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("Current user not found or invalid token");
        }

        return order.getUser().getId().equals(currentUser.getId()) || currentUser.hasRole("ADMIN");
    }

    /**
     * Checks if a user has any pending orders (shopping carts).
     *
     * @param userId The ID of the user to check.
     * @return True if the user has a PENDING order, false otherwise.
     * @see OrderStatus#PENDING
     */
    public boolean userHasPendingOrder(Long userId) {
        return orderRepository.existsByUserIdAndStatus(userId, OrderStatus.PENDING);
    }

    /**
     * Checks if a user has purchased a specific product.
     * This is determined by checking if the user has any DELIVERED orders that
     * contain the given product.
     *
     * @param userId    The ID of the user to check.
     * @param productId The ID of the product to check for purchase.
     * @return True if the user has purchased the product (i.e., it's in a DELIVERED
     *         order), false otherwise.
     * @see OrderStatus#DELIVERED
     */
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.findAllByUserId(userId).stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }
}
