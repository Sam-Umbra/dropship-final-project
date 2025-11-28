package br.dev.kajosama.dropship.domain.model.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * Represents a customer review for a product.
 * Each review is linked to a specific product and the user who wrote it.
 */
@Entity
@Table(name = "product_review")
public class Review {

    /**
     * The unique identifier for the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_review_id")
    private Long id;

    /**
     * The numerical rating given by the user, from 0 to 5.
     */
    @NotNull
    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    /**
     * The text content of the review. Mapped to a TEXT column in the database.
     */
    @Lob
    @NotBlank
    @Column(nullable = false)
    private String comment;

    /**
     * The product that this review belongs to.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The user who wrote this review.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * A collection of image URLs associated with the review.
     * Stored in a separate table `product_review_images`.
     */
    @ElementCollection
    @CollectionTable(name = "product_review_images", joinColumns = @JoinColumn(name = "product_review_id"))
    @Column(name = "image_url", nullable = false)
    private Set<String> imageUrls = new HashSet<>();

    /**
     * The timestamp when the review was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the review was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructs a new Review with the specified details.
     *
     * @param rating The numerical rating (0-5).
     * @param comment The text of the review.
     * @param imageUrls A set of URLs for images attached to the review.
     */
    public Review(@NotNull @Min(0) @Max(5) Integer rating, @NotBlank String comment, Set<String> imageUrls) {
        this.rating = rating;
        this.comment = comment;
        this.imageUrls = imageUrls;
    }

    /**
     * Default constructor required by JPA.
     */
    public Review() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(Set<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}