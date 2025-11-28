package br.dev.kajosama.dropship.domain.model.enums;

/**
 * Represents the availability and lifecycle status of a product.
 */
public enum ProductStatus {
    /**
     * The product is currently available for sale.
     */
    ACTIVE,
    /**
     * The product is temporarily not available for sale but may become active again.
     */
    INACTIVE,
    /**
     * The product is currently out of stock and cannot be purchased.
     */
    OUT_OF_STOCK,
    /**
     * The product is no longer sold and will not be restocked.
     */
    DISCONTINUED
}
