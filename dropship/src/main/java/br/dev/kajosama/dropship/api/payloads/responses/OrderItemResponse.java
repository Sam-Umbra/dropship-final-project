package br.dev.kajosama.dropship.api.payloads.responses;

import br.dev.kajosama.dropship.domain.model.entities.OrderItem;

/**
 * Represents the data transfer object for a single item within an order.
 *
 * @author Sam_Umbra
 * @param itemId The unique identifier for the order item.
 * @param quantity The quantity of the product in this order item.
 * @param product The detailed product information for this order item.
 */
public record OrderItemResponse(
        Long itemId,
        Integer quantity,
        ProductResponse product
        ) {

    /**
     * Creates an {@link OrderItemResponse} from an {@link OrderItem} entity.
     *
     * @param item The {@link OrderItem} entity to convert.
     * @return A new {@link OrderItemResponse} object.
     */
    public static OrderItemResponse fromEntity(OrderItem item) {
        ProductResponse product = ProductResponse.fromEntity(item.getProduct());

        return new OrderItemResponse(
                item.getId(),
                item.getQuantity(),
                product
        );
    }

}
