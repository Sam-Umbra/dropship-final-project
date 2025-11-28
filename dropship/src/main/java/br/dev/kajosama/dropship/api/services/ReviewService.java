package br.dev.kajosama.dropship.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.exceptions.EntityAlreadyExistsException;
import br.dev.kajosama.dropship.api.payloads.requests.ReviewRequest;
import br.dev.kajosama.dropship.api.payloads.requests.ReviewUpdateRequest;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.entities.Review;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.ReviewSortBy;
import br.dev.kajosama.dropship.domain.repositories.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Review} entities.
 *              Provides business logic for review-related operations such as
 *              adding,
 *              retrieving, updating, and deleting reviews. It interacts with
 *              {@link ReviewRepository},
 *              {@link ProductService}, and {@link OrderService}.
 */
@Service
public class ReviewService {

    /**
     * Repository for {@link Review} entities.
     */
    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Service for {@link Product} entities.
     */
    @Autowired
    private ProductService productService;

    /**
     * Service for {@link Order} entities.
     */
    @Autowired
    private OrderService orderService;

    /**
     * Retrieves a list of reviews for a specific product, sorted according to the
     * specified criteria.
     *
     * @param productId The ID of the product to retrieve reviews for.
     * @param sortBy    The sorting criteria for the reviews (e.g., NEWEST, OLDEST,
     *                  HIGHEST_RATING).
     * @return A {@link List} of {@link Review} objects for the specified product,
     *         sorted as requested.
     * @throws EntityNotFoundException If the product with the given ID is not
     *                                 found.
     */
    public List<Review> getReviewsForProduct(Long productId, ReviewSortBy sortBy) {
        productService.getProductById(productId);

        return switch (sortBy) {
            case NEWEST -> reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
            case OLDEST -> reviewRepository.findByProductIdOrderByCreatedAtAsc(productId);
            case HIGHEST_RATING -> reviewRepository.findByProductIdOrderByRatingDescCreatedAtDesc(productId);
            case LOWEST_RATING -> reviewRepository.findByProductIdOrderByRatingAscCreatedAtDesc(productId);
            default -> reviewRepository.findByProductIdOrderByCreatedAtDesc(productId); // Default to newest if sortBy
                                                                                        // is null or unrecognized
        };
    }

    /**
     * Saves a {@link Review} entity to the database.
     * This is a private helper method used internally by other service methods.
     *
     * @param review The {@link Review} entity to save.
     * @return The saved {@link Review} entity.
     */
    private Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    /**
     * Adds a new review for a product by a specific user.
     * Before adding, it checks if the user has already reviewed the product and if
     * the user has purchased the product.
     *
     * @param productId The ID of the product to review.
     * @param user      The {@link User} who is submitting the review.
     * @param request   The {@link ReviewRequest} containing the review details
     *                  (rating, comment, image URLs).
     * @return The newly created {@link Review} entity.
     * @throws EntityNotFoundException      If the product with the given ID is not
     *                                      found.
     * @throws EntityAlreadyExistsException If the user has already reviewed this
     *                                      product.
     * @throws AccessDeniedException        If the user has not purchased the
     *                                      product.
     */
    @Transactional
    public Review addReview(Long productId, User user, ReviewRequest request) {
        Product product = productService.getProductById(productId);

        if (reviewRepository.existsByProductIdAndUserId(productId, user.getId())) {
            throw new EntityAlreadyExistsException("Review", "product and user",
                    "User has already reviewed this product.");
        }

        if (!orderService.hasUserPurchasedProduct(user.getId(), productId)) {
            throw new AccessDeniedException("User cannot review a product that has not been purchased.");
        }

        Review newReview = new Review(request.rating(), request.comment(), request.imageUrls());
        newReview.setProduct(product);
        newReview.setUser(user);

        return saveReview(newReview);
    }

    /**
     * Deletes a review by its ID.
     * Only the owner of the review or an ADMIN can delete a review.
     *
     * @param reviewId The ID of the review to delete.
     * @param user     The {@link User} attempting to delete the review.
     * @throws EntityNotFoundException If the review with the given ID is not found.
     * @throws AccessDeniedException   If the user does not have permission to
     *                                 delete the review.
     */
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = getReviewById(reviewId);

        reviewUserMatchesCurrentUser(review, user, "delete");
        reviewRepository.delete(review);
    }

    /**
     * Updates an existing review.
     * Only the owner of the review or an ADMIN can update a review.
     * Fields like comment, rating, and image URLs can be updated.
     *
     * @param reviewId The ID of the review to update.
     * @param request  The {@link ReviewUpdateRequest} containing the updated review
     *                 details.
     * @param user     The {@link User} attempting to update the review.
     * @return The updated {@link Review} entity.
     * @throws EntityNotFoundException If the review with the given ID is not found.
     * @throws AccessDeniedException   If the user does not have permission to
     *                                 update the review.
     */
    @Transactional
    public Review updateReview(Long reviewId, ReviewUpdateRequest request, User user) {
        Review review = getReviewById(reviewId);

        reviewUserMatchesCurrentUser(review, user, "update");

        if (request.comment() != null && !request.comment().isBlank()) {
            review.setComment(request.comment());
        }

        if (request.rating() != null) {
            review.setRating(request.rating());
        }

        if (request.imageUrls() != null) {
            review.setImageUrls(request.imageUrls());
        }

        return saveReview(review);
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewId The ID of the review to retrieve.
     * @return The {@link Review} entity with the specified ID.
     * @throws EntityNotFoundException If the review with the given ID is not found.
     */
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));
    }

    /**
     * Checks if the user attempting an action on a review is either the owner of
     * the review or an ADMIN.
     * If not, an {@link AccessDeniedException} is thrown.
     *
     * @param review The {@link Review} entity being acted upon.
     * @param user   The {@link User} attempting the action.
     * @param msg    A string describing the action (e.g., "delete", "update") for
     *               the error message.
     * @throws AccessDeniedException If the user does not have permission to perform
     *                               the action on the review.
     */
    private void reviewUserMatchesCurrentUser(Review review, User user, String msg) {
        if (!review.getUser().getId().equals(user.getId()) && !user.hasRole("ADMIN")) {
            throw new AccessDeniedException(
                    "You can't " + msg + " a review that doesn't belong to you unless you are an ADMIN");
        }
    }
}
