package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents the association between a Supplier and a User.
 * This entity links a specific supplier to a user who manages or is associated with that supplier.
 */
@Entity
@Table(name = "supplier_user")
public class SupplierUser {

    /**
     * The unique identifier for the supplier-user association.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_user_id")
    private Long id;

    /**
     * The supplier associated with this entry.
     * This is a many-to-one relationship with the Supplier entity.
     * {@code @JsonBackReference} is used to prevent infinite recursion in JSON serialization.
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /**
     * The user associated with this entry.
     * This is a many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The timestamp when the association was created.
     * Automatically set upon creation.
     */
    @Column(name = "association_date")
    @CreationTimestamp
    private LocalDateTime associationDate;

    /**
     * Default constructor required by JPA.
     */
    public SupplierUser() {
    }

    /**
     * Constructs a new SupplierUser association with the specified details.
     *
     * @param id The unique identifier for the association.
     * @param supplier The supplier involved in the association.
     * @param user The user involved in the association.
     * @param associationDate The date and time when the association was made.
     */
    public SupplierUser(Long id, Supplier supplier, User user, LocalDateTime associationDate) {
        this.id = id;
        this.supplier = supplier;
        this.user = user;
        this.associationDate = associationDate;
    }

    /**
     * Returns the unique identifier for the supplier-user association.
     *
     * @return The ID of the association.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for the supplier-user association.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the supplier associated with this entry.
     *
     * @return The Supplier object.
     */
    public Supplier getSupplier() {
        return this.supplier;
    }

    /**
     * Sets the supplier for this association.
     *
     * @param supplier The Supplier object to set.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the user associated with this entry.
     *
     * @return The User object.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the user for this association.
     *
     * @param user The User object to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the timestamp when the association was created.
     *
     * @return The LocalDateTime of the association.
     */
    public LocalDateTime getAssociationDate() {
        return this.associationDate;
    }

    /**
     * Sets the timestamp when the association was created.
     *
     * @param associationDate The LocalDateTime to set.
     */
    public void setAssociationDate(LocalDateTime associationDate) {
        this.associationDate = associationDate;
    }

    /**
     * Sets the ID and returns the current instance for method chaining.
     *
     * @param id The ID to set.
     * @return The current SupplierUser instance.
     */
    public SupplierUser id(Long id) {
        setId(id);
        return this;
    }

    /**
     * Sets the supplier and returns the current instance for method chaining.
     *
     * @param supplier The Supplier object to set.
     * @return The current SupplierUser instance.
     */
    public SupplierUser supplier(Supplier supplier) {
        setSupplier(supplier);
        return this;
    }

    /**
     * Sets the user and returns the current instance for method chaining.
     *
     * @param user The User object to set.
     * @return The current SupplierUser instance.
     */
    public SupplierUser user(User user) {
        setUser(user);
        return this;
    }

    /**
     * Sets the association date and returns the current instance for method chaining.
     *
     * @param associationDate The LocalDateTime to set.
     * @return The current SupplierUser instance.
     */
    public SupplierUser associationDate(LocalDateTime associationDate) {
        setAssociationDate(associationDate);
        return this;
    }

    /**
     * Compares this SupplierUser object with another object for equality.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SupplierUser)) {
            return false;
        }
        SupplierUser supplierUser = (SupplierUser) o;
        return Objects.equals(id, supplierUser.id) && Objects.equals(supplier, supplierUser.supplier) && Objects.equals(user, supplierUser.user) && Objects.equals(associationDate, supplierUser.associationDate);
    }

    /**
     * Returns the hash code for this SupplierUser object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, supplier, user, associationDate);
    }

    @Override
    public String toString() {
        // To prevent potential circular references in toString() for related entities,
        // it's often better to log only their IDs or a minimal representation.
        // However, for simplicity and given the current context, full objects are logged.
        // If performance or stack overflow issues arise, consider logging only IDs:
        // ", supplier='" + (getSupplier() != null ? getSupplier().getId() : "null") + "'"
        return "{"
                + " id='" + getId() + "'"
                + ", supplier='" + getSupplier() + "'"
                + ", user='" + getUser() + "'"
                + ", associationDate='" + getAssociationDate() + "'"
                + "}";
    }

}