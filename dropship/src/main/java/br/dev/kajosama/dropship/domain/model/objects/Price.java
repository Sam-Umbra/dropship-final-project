package br.dev.kajosama.dropship.domain.model.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Price {
    
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    public Price () {}

    public Price (BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return amount.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;
        Price price = (Price) o;
        return amount.equals(price.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    public Price add(Price other) {
        return new Price(this.amount.add(other.amount));
    }

    public Price subtract(Price other) {
        return new Price(this.amount.subtract(other.amount));
    }

    public Price multiply(BigDecimal factor) {
        return new Price(this.amount.multiply(factor));
    }

    public Price multiply(Integer factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public Price divide(BigDecimal factor) {
        return new Price(this.amount.divide(factor));
    }

    public static Price of(BigDecimal amount) {
        return new Price(amount);
    }

    public static Price of(double amount) {
        return new Price(BigDecimal.valueOf(amount));
    }

    public static Price of(long amount) {
        return new Price(BigDecimal.valueOf(amount));
    }
    
}
