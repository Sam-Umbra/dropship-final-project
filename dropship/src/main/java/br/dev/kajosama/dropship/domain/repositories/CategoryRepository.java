package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Category;

import java.util.Optional;

/**
 * Repository interface for managing {@link Category} entities.
 * Provides methods for performing CRUD operations and custom queries related to categories.
 * @author Sam_Umbra
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
 * Finds a category by its name.
 *
 * @param name The name of the category to find.
 * @return An {@link Optional} containing the found {@link Category} if it exists,
 * or an empty {@link Optional} if no category with the given name is found.
 */
    Optional<Category> findByName(String name);
}
