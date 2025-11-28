package br.dev.kajosama.dropship.domain.model.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a user's favorite product.
 * This entity links a specific user to a specific product they have marked as a
 * favorite.
 */
@Entity
@Table(name = "favorites")
public class Favorites {

    /**
     * The unique identifier for the favorite entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    /**
     * The user who marked the product as a favorite.
     * This is a required field.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The product that was marked as a favorite.
     * This is a required field.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Default constructor required by JPA.
     */
    public Favorites() {
    }

    /**
     * Constructs a new Favorites entry with the specified user and product.
     *
     * @param user    The user who marked the product as a favorite.
     * @param product The product that was marked as a favorite.
     */
    public Favorites(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    /**
     * Returns the unique identifier for the favorite entry.
     *
     * @return The ID of the favorite entry.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for the favorite entry.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user who marked the product as a favorite.
     *
     * @return The user.
     */
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the product that was marked as a favorite.
     *
     * @return The product.
     */
    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Sets the unique identifier for the favorite entry and returns the current
     * instance.
     *
     * @param id The ID to set.
     * @return The current Favorites instance.
     */
    public Favorites id(Long id) {
        setId(id);
        return this;
    }

    /**
     * Sets the user for the favorite entry and returns the current instance.
     *
     * @param user The user to set.
     * @return The current Favorites instance.
     */
    public Favorites user(User user) {
        setUser(user);
        return this;
    }

    /**
     * Sets the product for the favorite entry and returns the current instance.
     * 
     * @param product The product to set.
     * @return The current Favorites instance.
     */
    public Favorites product(Product product) {
        setProduct(product);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Favorites)) {
            return false;
        }
        Favorites favorites = (Favorites) o;
        return Objects.equals(id, favorites.id) && Objects.equals(user, favorites.user)
                && Objects.equals(product, favorites.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, product); // NOSONAR
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", user='" + getUser().getId() + "'" + // Only log user ID to prevent circular reference
                ", product='" + getProduct().getId() + "'" + // Only log product ID to prevent circular reference
                "}";
    }

}
