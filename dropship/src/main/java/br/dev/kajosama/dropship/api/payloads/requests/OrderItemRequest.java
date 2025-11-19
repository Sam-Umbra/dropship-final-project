package br.dev.kajosama.dropship.api.payloads.requests;

public record OrderItemRequest(
    Integer quantity,
    Long productId
) {

}
