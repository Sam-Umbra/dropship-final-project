package br.dev.kajosama.dropship.api.payloads.responses;

import br.dev.kajosama.dropship.domain.model.entities.OrderItem;

public record OrderItemResponse(
    Long itemId,
    Integer quantity,
    ProductResponse product
) {

    public static OrderItemResponse fromEntity(OrderItem item) {
        ProductResponse product = ProductResponse.fromEntity(item.getProduct());

        return new OrderItemResponse(
            item.getId(), 
            item.getQuantity(), 
            product
        );
    }

}
