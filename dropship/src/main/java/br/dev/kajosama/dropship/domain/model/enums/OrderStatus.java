package br.dev.kajosama.dropship.domain.model.enums;

/**
 * Represents the different stages of an order's lifecycle, from creation to completion or cancellation.
 */
public enum OrderStatus {
    /**
     * The order has been created but payment has not yet been processed.
     * This is typically the status of a user's shopping cart before checkout.
     */
    PENDING,
    /**
     * The order has been successfully paid by the customer.
     */
    PAID,
    /**
     * The order has been paid and is now being prepared for shipment by the supplier.
     */
    PROCESSING,
    /**
     * The order has been dispatched by the supplier and is in transit to the customer.
     */
    SHIPPED,
    /**
     * The order has been successfully delivered to the customer. This is a final, successful state.
     */
    DELIVERED,
    /**
     * The order has been cancelled by the user or an administrator. This is a final state.
     */
    CANCELLED,
    /**
     * The payment for the order has been returned to the customer. This is a final state.
     */
    REFUNDED
}
