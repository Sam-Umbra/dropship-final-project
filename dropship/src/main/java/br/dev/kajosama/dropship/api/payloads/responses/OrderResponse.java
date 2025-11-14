package br.dev.kajosama.dropship.api.payloads.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;

public record OrderResponse(
    Long id,
    LocalDateTime orderDate,
    OrderStatus status,
    BigDecimal totalAmount,
    List<OrderItemResponse> orderItems
) {

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
