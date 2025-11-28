package br.dev.kajosama.dropship.domain.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.User;

/**
 * Repository interface for managing {@link Favorites} entities.
 * Provides methods for performing CRUD operations and custom queries related to user favorites.
 * @author Sam_Umbra
 */
@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    /**
 * Finds a list of favorite entries for a specific user.
 *
 * @param user The {@link User} whose favorites are to be found.
 * @return A list of {@link Favorites} entities belonging to the specified user.
 */
    List<Favorites> findByUser(User user);

    /**
 * Finds a specific favorite entry by user and product ID.
 *
 * @param user The {@link User} who owns the favorite.
 * @param productId The ID of the product that is favorited.
 * @return An {@link Optional} containing the found {@link Favorites} entry if it exists,
 * or an empty {@link Optional} if no such favorite is found.
 */
    Optional<Favorites> findByUserAndProductId(User user, Long productId);

    /**
 * Checks if a favorite entry exists for a given user and product ID.
 *
 * @param user The {@link User} to check.
 * @param productId The ID of the product to check.
 * @return True if a favorite entry exists for the user and product, false otherwise.
 */
    boolean existsByUserAndProductId(User user, Long productId);

    /**
 * Finds a list of favorite entries for a specific user and a collection of product IDs.
 *
 * @param user The {@link User} whose favorites are to be found.
 * @param productIds A {@link Collection} of product IDs to search for.
 * @return A list of {@link Favorites} entities belonging to the specified user and product IDs.
 */
    List<Favorites> findByUserAndProductIdIn(User user, Collection<Long> productIds);

}