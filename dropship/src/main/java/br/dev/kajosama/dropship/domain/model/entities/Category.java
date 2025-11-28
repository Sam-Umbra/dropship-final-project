package br.dev.kajosama.dropship.domain.model.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a product category in the system.
 * Categories can be organized hierarchically with parent and sub-categories.
 */
@Entity
@Table(name = "categories")
public class Category {

    /**
     * The unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /**
     * The name of the category. Must be unique and not null.
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * The parent category, allowing for hierarchical categorization.
     * A null value indicates a top-level category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    /**
     * A set of sub-categories belonging to this category.
     * Changes to this category cascade to its sub-categories.
     */
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private Set<Category> subCategories = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Category() {
    }

    /**
     * Constructs a new Category with the specified name and parent category.
     *
     * @param name           The name of the category.
     * @param parentCategory The parent category, or null if it's a top-level
     *                       category.
     */
    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    /**
     * Returns the unique identifier of the category.
     * 
     * @return The category ID.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier of the category.
     * 
     * @param id The category ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the category.
     * 
     * @return The category name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the category.
     * 
     * @param name The category name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the parent category.
     * 
     * @return The parent category, or null if it's a top-level category.
     */
    public Category getParentCategory() {
        return this.parentCategory;
    }

    /**
     * Sets the parent category.
     * 
     * @param parentCategory The parent category to set.
     */
    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    /**
     * Returns the set of sub-categories.
     * 
     * @return A set of sub-categories.
     */
    public Set<Category> getSubCategories() {
        return this.subCategories;
    }

    /**
     * Sets the set of sub-categories.
     * 
     * @param subCategories The set of sub-categories to set.
     */
    public void setSubCategories(Set<Category> subCategories) {
        this.subCategories = subCategories;
    }

}
