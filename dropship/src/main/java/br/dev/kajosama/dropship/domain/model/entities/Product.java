package br.dev.kajosama.dropship.domain.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

import br.dev.kajosama.dropship.domain.interfaces.Auditable;
import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;
import br.dev.kajosama.dropship.domain.model.objects.Price;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Auditable
@Entity
@Table(name = "products")
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    /**
     * The supplier of this product.
     */
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /**
     * The name of the product.
     * Must be between 5 and 80 characters long.
     */
    @NotBlank
    @Size(max = 80, min = 5)
    @Column(name = "product_name", nullable = false, length = 80)
    private String name;

    /**
     * A detailed description of the product.
     * Stored as a large object (LOB) in the database.
     */
    @NotBlank
    @Column(nullable = false)
    @Lob
    private String description;

    /**
     * The price of the product.
     * This is an embedded object representing a monetary value.
     */
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price", precision = 10, scale = 2, nullable = false))
    })
    private Price price;

    /**
     * The current stock quantity of the product.
     */
    @NotNull
    private Integer stock;

    /**
     * The current status of the product (e.g., ACTIVE, INACTIVE, OUT_OF_STOCK).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    /**
     * The timestamp when the product was last updated.
     * Automatically updated on entity modification.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The URL of the main image for the product.
     */
    @NotNull
    private String imgUrl;

    /**
     * The discount percentage applied to the product.
     * Value ranges from 0.0 to 100.0.
     */
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    @Column(name = "discount", precision = 5, scale = 2, nullable = false)
    private BigDecimal discount;

    /**
     * The set of categories this product belongs to.
     * This is a many-to-many relationship managed through a join table.
     */
    @ManyToMany
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Product() {
    }

    /**
     * Constructs a new Product with the specified details.
     *
     * @param name        The name of the product.
     * @param description A detailed description of the product.
     * @param price       The price of the product.
     * @param stock       The current stock quantity.
     * @param imgUrl      The URL of the main image.
     * @param discount    The discount percentage.
     */
    public Product(String name, String description, Price price, Integer stock, String imgUrl, BigDecimal discount) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imgUrl = imgUrl;
        this.discount = discount;
    }

    /**
     * Returns the unique identifier for the product.
     *
     * @return The ID of the product.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for the product.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the product.
     *
     * @return The product name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name The product name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the detailed description of the product.
     *
     * @return The product description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the detailed description of the product.
     *
     * @param description The product description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the price of the product.
     *
     * @return The Price object.
     */
    public Price getPrice() {
        return this.price;
    }

    /**
     * Sets the price of the product.
     *
     * @param price The Price object to set.
     */
    public void setPrice(Price price) {
        this.price = price;
    }

    /**
     * Returns the current stock quantity of the product.
     *
     * @return The stock quantity.
     */
    public Integer getStock() {
        return this.stock;
    }

    /**
     * Sets the current stock quantity of the product.
     *
     * @param stock The stock quantity to set.
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Returns the current status of the product.
     *
     * @return The ProductStatus enum.
     */
    public ProductStatus getStatus() {
        return this.status;
    }

    /**
     * Sets the current status of the product.
     *
     * @param status The ProductStatus enum to set.
     */
    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    /**
     * Returns the timestamp when the product was last updated.
     *
     * @return The LocalDateTime of the last update.
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * Sets the timestamp when the product was last updated.
     *
     * @param updatedAt The LocalDateTime to set.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the URL of the main image for the product.
     *
     * @return The image URL string.
     */
    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * Returns the discount percentage applied to the product.
     *
     * @return The discount percentage as a BigDecimal.
     */
    public BigDecimal getDiscount() {
        return this.discount;
    }

    /**
     * Sets the discount percentage applied to the product.
     *
     * @param discount The discount percentage to set.
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Returns the set of categories this product belongs to.
     *
     * @return A Set of Category objects.
     */
    public Set<Category> getCategories() {
        return this.categories;
    }

    /**
     * Sets the set of categories this product belongs to.
     *
     * @param categories The Set of Category objects to set.
     */
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * Returns the supplier of this product.
     *
     * @return The Supplier object.
     */
    public Supplier getSupplier() {
        return this.supplier;
    }

    /**
     * Sets the supplier of this product.
     *
     * @param supplier The Supplier object to set.
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
