package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Review;

/**
 * Repository interface for managing {@link Review} entities.
 * Provides methods for performing CRUD operations and custom queries related to product reviews.
 * @author Sam_Umbra
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    /**
 * Finds a list of reviews for a specific product, ordered by creation timestamp in descending order.
 *
 * @param productId The ID of the product whose reviews are to be found.
 * @return A list of {@link Review} entities for the specified product, newest first.
 */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
 * Finds a list of reviews for a specific product, ordered by creation timestamp in ascending order.
 *
 * @param productId The ID of the product whose reviews are to be found.
 * @return A list of {@link Review} entities for the specified product, oldest first.
 */
    List<Review> findByProductIdOrderByCreatedAtAsc(Long productId);

    /**
 * Finds a list of reviews for a specific product, ordered by rating in descending order
 * and then by creation timestamp in descending order.
 *
 * @param productId The ID of the product whose reviews are to be found.
 * @return A list of {@link Review} entities for the specified product, highest rating first, then newest.
 */
    List<Review> findByProductIdOrderByRatingDescCreatedAtDesc(Long productId);

    /**
 * Finds a list of reviews for a specific product, ordered by rating in ascending order
 * and then by creation timestamp in descending order.
 *
 * @param productId The ID of the product whose reviews are to be found.
 * @return A list of {@link Review} entities for the specified product, lowest rating first, then newest.
 */
    List<Review> findByProductIdOrderByRatingAscCreatedAtDesc(Long productId);

    /**
 * Checks if a review exists for a given product by a specific user.
 *
 * @param productId The ID of the product to check.
 * @param userId The ID of the user to check.
 * @return True if a review exists for the product by the specified user, false otherwise.
 */
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
