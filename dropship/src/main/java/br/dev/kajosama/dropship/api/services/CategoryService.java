package br.dev.kajosama.dropship.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.entities.Category;
import br.dev.kajosama.dropship.domain.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Category} entities.
 *              Provides business logic for category-related operations such as
 *              retrieving,
 *              saving, and deleting categories. It interacts with
 *              {@link CategoryRepository}.
 */
@Service
@Transactional
public class CategoryService {

    /**
     * Repository for {@link Category} entities.
     */
    @Autowired
    CategoryRepository categoryRepo;

    /**
     * Checks if a category exists by its ID.
     *
     * @param id The ID of the category to check.
     * @return True if a category with the specified ID exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return categoryRepo.existsById(id);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @throws EntityNotFoundException If the category with the given ID is not
     *                                 found.
     */
    public void deleteCategoryById(Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Category with ID: {" + id + "} NOT FOUND");
        }
        categoryRepo.deleteById(id);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return The {@link Category} entity with the specified ID.
     * @throws EntityNotFoundException If the category with the given ID is not
     *                                 found.
     */
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID: {" + id + "} NOT FOUND"));
    }

    /**
     * Retrieves a list of all categories.
     *
     * @return A {@link List} of all {@link Category} entities.
     */
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    /**
     * Saves a {@link Category} entity to the database.
     *
     * @param category The {@link Category} entity to save.
     * @return The saved {@link Category} entity.
     */
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    /**
     * Retrieves a list of all top-level categories (categories without a parent).
     *
     * @return A {@link List} of {@link Category} entities that are parent
     *         categories.
     */
    public List<Category> getParentCategories() {
        return categoryRepo.findAll().stream()
                .filter(cat -> cat.getParentCategory() == null)
                .toList();
    }

}
