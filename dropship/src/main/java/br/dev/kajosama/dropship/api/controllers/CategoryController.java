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

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getCategoryById(Long id) {
        Category cat = categoryService.getCategoryById(id);
        return CategoryResponse.fromEntity(cat);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category registerCategory(@Valid @RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.getCategoryById(id);
        updatedCategory.setName(category.getName());
        updatedCategory.setParentCategory(category.getParentCategory());

        return ResponseEntity.ok(categoryService.saveCategory(updatedCategory));
    }

}
