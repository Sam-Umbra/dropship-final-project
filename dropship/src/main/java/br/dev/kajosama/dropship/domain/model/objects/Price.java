package br.dev.kajosama.dropship.domain.model.objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Represents a monetary value in a safe and immutable way.
 * This class is a "Value Object" and can be embedded in other JPA entities.
 * Ensures that all monetary values have a precision of 2 decimal places.
 */
@Embeddable
public class Price {

    /**
     * The price amount, stored with high precision.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Protected default constructor, required by JPA. Should not be used directly.
     */
    protected Price() {}

    /**
     * Main constructor that creates a Price instance from a BigDecimal.
     *
     * @param amount The monetary value. Cannot be null.
     * @throws IllegalArgumentException if the value is null.
     */
    public Price(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        // Ensures the value is always stored with 2 decimal places.
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return The monetary value as a BigDecimal.
     */
    public BigDecimal getAmount() {
        return this.amount;
    }
    
    /**
     * Adds another price to this one and returns a new Price object with the result.
     * @param other The price to be added.
     * @return A new Price object representing the sum.
     */
    public Price add(Price other) {
        BigDecimal result = this.amount.add(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    /**
     * Subtracts another price from this one and returns a new Price object with the result.
     * @param other The price to be subtracted.
     * @return A new Price object representing the difference.
     */
    public Price subtract(Price other) {
        BigDecimal result = this.amount.subtract(other.amount).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    /**
     * Multiplies this price by a factor and returns a new Price object.
     * @param factor The multiplication factor.
     * @return A new Price object representing the product.
     */
    public Price multiply(BigDecimal factor) {
        BigDecimal result = this.amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    /**
     * Multiplies this price by an integer factor.
     * @param factor The integer multiplication factor.
     * @return A new Price object representing the product.
     */
    public Price multiply(Integer factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Divides this price by a factor.
     * @param factor The divisor.
     * @return A new Price object representing the quotient.
     */
    public Price divide(BigDecimal factor) {
        BigDecimal result = this.amount.divide(factor, 2, RoundingMode.HALF_UP);
        return new Price(result);
    }

    /**
     * Checks if the price is zero.
     * @return true if the value is zero, false otherwise.
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Checks if this price is greater than another.
     * @param other The other price for comparison.
     * @return true if this price is greater, false otherwise.
     */
    public boolean isGreaterThan(Price other) {
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Checks if this price is less than another.
     * @param other The other price for comparison.
     * @return true if this price is less, false otherwise.
     */
    public boolean isLessThan(Price other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Static factory method to create a Price from a BigDecimal.
     * @param amount The value.
     * @return A new Price instance.
     */
    public static Price of(BigDecimal amount) {
        return new Price(amount);
    }

    /**
     * Static factory method to create a Price from a double.
     * @param amount The value.
     * @return A new Price instance.
     */
    public static Price of(double amount) {
        return new Price(BigDecimal.valueOf(amount));
    }

    /**
     * Static factory method to create a Price from a long.
     * @param amount The value.
     * @return A new Price instance.
     */
    public static Price of(long amount) {
        return new Price(BigDecimal.valueOf(amount));
    }

    @Override
    public String toString() {
        return this.amount.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        // Compares the numerical value, ignoring differences in scale (e.g., 5.50 is equal to 5.5)
        return amount.compareTo(price.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }

}