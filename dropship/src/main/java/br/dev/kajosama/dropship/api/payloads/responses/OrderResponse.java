package br.dev.kajosama.dropship.api.payloads.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;

/**
 * Represents the data transfer object for a customer order.
 *
 * @author Sam_Umbra
 * @param id The unique identifier for the order.
 * @param orderDate The date and time the order was placed.
 * @param status The current status of the order (e.g., PENDING, COMPLETED).
 * @param totalAmount The total cost of the order.
 * @param orderItems A list of items included in the order.
 */
public record OrderResponse(
        Long id,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> orderItems
        ) {

    /**
     * Creates an {@link OrderResponse} from an {@link Order} entity.
     *
     * @param order The {@link Order} entity to convert.
     * @return A new {@link OrderResponse} object.
     */
    public static OrderResponse fromEntity(Order order) {
        BigDecimal totalAmount = order.getTotal().getAmount();
        List<OrderItemResponse> items = order.getItems()
                .stream().map(OrderItemResponse::fromEntity)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                totalAmount,
                items
        );
    }

}
