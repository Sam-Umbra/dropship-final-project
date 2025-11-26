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

@Entity
@Table(name = "favorites")
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;


    public Favorites() {
    }

    public Favorites(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Favorites id(Long id) {
        setId(id);
        return this;
    }

    public Favorites user(User user) {
        setUser(user);
        return this;
    }

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
        return Objects.equals(id, favorites.id) && Objects.equals(user, favorites.user) && Objects.equals(product, favorites.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, product);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", user='" + getUser() + "'" +
            ", product='" + getProduct() + "'" +
            "}";
    }
    
}
