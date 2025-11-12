package br.dev.kajosama.dropship.domain.model.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Price {

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    protected Price() {}

    public Price(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Price add(Price other) {
        BigDecimal result = this.amount.add(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    public Price subtract(Price other) {
        BigDecimal result = this.amount.subtract(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    public Price multiply(BigDecimal factor) {
        BigDecimal result = this.amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    public Price multiply(Integer factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public Price divide(BigDecimal factor) {
        BigDecimal result = this.amount.divide(factor, 2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Price other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Price other) {
        return this.amount.compareTo(other.amount) < 0;
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

    @Override
    public String toString() {
        return amount.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return amount.compareTo(price.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }
}