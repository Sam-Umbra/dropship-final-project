package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;
import br.dev.kajosama.dropship.domain.model.objects.Price;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import br.dev.kajosama.dropship.domain.interfaces.Auditable;

@Entity
@Table(name = "orders")
@Auditable
public class Order {

    /**
     * The unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    /**
     * The date and time when the order was placed.
     * This field is automatically set upon creation and cannot be updated.
     */
    @NotNull
    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    /**
     * The current status of the order (e.g., PENDING, SHIPPED, DELIVERED).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * The total amount of the order, including all items and discounts.
     * This is an embedded object representing a monetary value.
     */
    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_amount", precision = 10, scale = 2, nullable = false))
    })
    private Price total = Price.of(0);

    /**
     * A list of items included in this order.
     * Changes to the order cascade to its items, and orphaned items are removed.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "order",
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    /**
     * The user who placed this order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Default constructor required by JPA.
     */
    public Order() {
    }

    /**
     * Constructs a new Order with the specified user and initial status.
     *
     * @param user The user placing the order.
     * @param status The initial status of the order.
     */
    public Order(User user, OrderStatus status) {
        this.user = user;
        this.status = status;
    }

    /**
     * Returns the unique identifier for the order.
     *
     * @return The ID of the order.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the date and time when the order was placed.
     *
     * @return The order date.
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the date and time when the order was placed.
     *
     * @param time The order date to set.
     */
    public void setOrderDate(LocalDateTime time) {
        this.orderDate = time;
    }

    /**
     * Returns the current status of the order.
     *
     * @return The order status.
     */
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Returns the total amount of the order.
     *
     * @return The total price of the order.
     */
    public Price getTotal() {
        return total;
    }

    /**
     * Returns the list of items in the order.
     *
     * @return A list of OrderItem objects.
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Returns the user who placed the order.
     *
     * @return The User object associated with the order.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user for this order.
     *
     * @param user The User object to associate with the order.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Adds an item to the order and recalculates the total.
     *
     * @param item The OrderItem to add.
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            return;
        }
        item.setOrder(this);
        this.items.add(item);
        recalculateTotal();
    }

    /**
     * Removes an item from the order and recalculates the total.
     *
     * @param item The OrderItem to remove.
     */
    public void removeItem(OrderItem item) {
        if (item == null) {
            return;
        }
        this.items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    /**
     * Recalculates the total price of the order based on its current items.
     */
    public void recalculateTotal() {
        this.total = items.stream()
                .map(OrderItem::totalPrice)
                .reduce(Price.of(0), Price::add);
    }

    /**
     * Sets the list of items for this order.
     * Note: This method does not automatically recalculate the total.
     * It is recommended to use {@link #addItem(OrderItem)} and {@link #removeItem(OrderItem)}
     * for managing items to ensure the total is always up-to-date.
     *
     * @param items The list of OrderItem objects to set.
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}