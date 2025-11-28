package br.dev.kajosama.dropship.api.payloads.requests;

/**
 * Represents a request to add or update an item within an order.
 *
 * @author Sam_Umbra
 * @param quantity The quantity of the product to be ordered.
 * @param productId The ID of the product.
 */
public record OrderItemRequest(
        Integer quantity,
        Long productId
        ) {

}
