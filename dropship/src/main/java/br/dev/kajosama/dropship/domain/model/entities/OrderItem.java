package br.dev.kajosama.dropship.domain.model.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import br.dev.kajosama.dropship.domain.model.objects.Price;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an item within an order.
 * Each order item links a specific product to an order,
 * including the quantity and the price at the time of purchase.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    /**
     * The unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    /**
     * The product associated with this order item.
     * This is a lazy-fetched many-to-one relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The quantity of the product in this order item.
     * Must not be null.
     */
    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    /**
     * The price of the product at the time it was added to the order.
     * This is an embedded object representing a monetary value.
     */
    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price_amount", precision = 10, scale = 2, nullable = false))
    })
    private Price price;

    /**
     * The order to which this item belongs.
     * This is a lazy-fetched many-to-one relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Default constructor required by JPA.
     */
    public OrderItem() {}

    /**
     * Constructs a new OrderItem with the specified product and quantity.
     * The price of the item is automatically set from the product's current price.
     *
     * @param product The product being ordered.
     * @param quantity The quantity of the product.
     */
    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
    }

    /**
     * Returns the unique identifier for the order item.
     * @return The ID of the order item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the product associated with this order item.
     * @return The Product object.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product for this order item and updates its price accordingly.
     * @param product The Product object to set.
     */
    public void setProduct(Product product) {
        this.product = product;
        this.price = product != null ? product.getPrice() : Price.of(0);
    }

    /**
     * Returns the quantity of the product in this order item.
     * @return The quantity.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product in this order item.
     * @param quantity The quantity to set.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the price of the product at the time it was added to the order.
     * @return The Price object.
     */
    public Price getPrice() {
        return price;
    }

    /**
     * Sets the price for this order item.
     * @param price The Price object to set.
     */
    public void setPrice(Price price) {
        this.price = price;
    }

    /**
     * Returns the order to which this item belongs.
     * @return The Order object.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order for this order item. This method is protected
     * as it should primarily be managed internally by the Order entity.
     * @param order The Order object to set.
     */
    protected void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Calculates the total price for this order item, considering the product's
     * price, quantity, and any applicable discount.
     *
     * @return The total Price for this order item.
     */
    public Price totalPrice() {
        if (product == null) return Price.of(0);
        Price basePrice = product.getPrice();

        BigDecimal discountPercent = Optional.ofNullable(product.getDiscount())
                .orElse(BigDecimal.ZERO);

        // Calculate the multiplier for the discounted price
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );

        // Ensure discount multiplier does not go below zero
        if (discountMultiplier.compareTo(BigDecimal.ZERO) < 0) {
            discountMultiplier = BigDecimal.ZERO;
        }

        // Apply discount and then multiply by quantity
        Price discounted = basePrice.multiply(discountMultiplier);
        return discounted.multiply(quantity);
    }
}