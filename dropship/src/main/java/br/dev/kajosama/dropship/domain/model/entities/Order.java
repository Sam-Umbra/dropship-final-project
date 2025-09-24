package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;
import br.dev.kajosama.dropship.domain.model.objects.Price;
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

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @NotNull
    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull
    @Embedded
    private Price total = Price.of(0);

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> itens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /*
     * private Long shippingAddressId;
     */

    public Order () {}


    public Order(Long id, LocalDateTime orderDate, OrderStatus status, Price total, List<OrderItem> itens, User user) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.total = total;
        this.itens = itens;
        this.user = user;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Price getTotal() {
        return this.total;
    }

    public void recalculateTotal() {
        this.total = itens.stream()
                .map(OrderItem::totalPrice)
                .reduce(Price.of(0), Price::add);
    }

    public List<OrderItem> getItens() {
        return this.itens;
    }

    public void setItens(List<OrderItem> itens) {
        this.itens = itens;
    }

    public void addItem(OrderItem item) {
        this.itens.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        itens.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }


    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
