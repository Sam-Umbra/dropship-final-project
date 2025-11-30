package br.dev.kajosama.dropship.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.responses.CategoryResponse;
import br.dev.kajosama.dropship.api.services.CategoryService;
import br.dev.kajosama.dropship.domain.model.entities.Category;
import jakarta.validation.Valid;

/**
 * REST controller for managing product categories. Provides endpoints for
 * creating, retrieving, updating, and deleting categories.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    /**
     * Service for handling category-related business logic.
     */
    @Autowired
    CategoryService categoryService;

    /**
     * Retrieves all parent categories (categories without a parent).
     *
     * @return A {@link ResponseEntity} containing a list of
     * {@link CategoryResponse} objects for parent categories, or 404 Not Found
     * if no parent categories exist.
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        if (categoryService.getParentCategories().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryService.getParentCategories()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList()
        );
    }

    /**
     * Retrieves a single category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return A {@link CategoryResponse} object representing the category.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(Long id) {
        Category cat = categoryService.getCategoryById(id);
        return CategoryResponse.fromEntity(cat);
    }

    /**
     * Registers a new category.
     *
     * @param category The {@link Category} object to be created. Must be valid.
     * @return The saved {@link Category} entity.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category registerCategory(@Valid @RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing category. It retrieves the category by its ID and
     * updates its name and parent category with the values from the request
     * body.
     *
     * @param id The ID of the category to update.
     * @param category The {@link Category} object containing the new data.
     * @return A {@link ResponseEntity} containing the updated {@link Category}
     * entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.getCategoryById(id);
        updatedCategory.setName(category.getName());
        updatedCategory.setParentCategory(category.getParentCategory());

        return ResponseEntity.ok(categoryService.saveCategory(updatedCategory));
    }

}
