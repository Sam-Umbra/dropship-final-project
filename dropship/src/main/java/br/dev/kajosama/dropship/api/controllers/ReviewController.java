package br.dev.kajosama.dropship.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.requests.ReviewRequest;
import br.dev.kajosama.dropship.api.payloads.requests.ReviewUpdateRequest;
import br.dev.kajosama.dropship.api.payloads.responses.ReviewResponse;
import br.dev.kajosama.dropship.api.services.ReviewService;
import br.dev.kajosama.dropship.domain.model.entities.Review;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.ReviewSortBy;
import jakarta.validation.Valid;

/**
 * REST controller for managing product reviews. Provides endpoints for
 * creating, retrieving, updating, and deleting reviews for products.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/product")
public class ReviewController {

    /**
     * Service for handling review-related business logic.
     */
    @Autowired
    private ReviewService reviewService;

    /**
     * Retrieves all reviews for a specific product, with optional sorting.
     *
     * @param productId The ID of the product whose reviews are to be retrieved.
     * @param sortBy The sorting criteria for the reviews (e.g., NEWEST,
     * HIGHEST_RATING). Defaults to NEWEST.
     * @return A {@link ResponseEntity} containing a list of
     * {@link ReviewResponse} objects.
     */
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProduct(@PathVariable Long productId,
            @RequestParam(name = "sort", defaultValue = "NEWEST") ReviewSortBy sortBy) {
        List<ReviewResponse> reviews = reviewService.getReviewsForProduct(productId, sortBy).stream()
                .map(ReviewResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    /**
     * Adds a new review to a product.
     *
     * @param productId The ID of the product to review.
     * @param request The review content and rating.
     * @param user The authenticated user posting the review, injected by Spring
     * Security.
     * @return A {@link ResponseEntity} containing the created
     * {@link ReviewResponse} with a 201 Created status.
     */
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewResponse> saveReview(@PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal User user) {
        Review savedReview = reviewService.addReview(productId, user, request);
        ReviewResponse response = ReviewResponse.fromEntity(savedReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Deletes a review. The user can only delete their own reviews.
     *
     * @param reviewId The ID of the review to delete.
     * @param user The authenticated user attempting to delete the review.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing review. The user can only update their own reviews.
     *
     * @param reviewId The ID of the review to update.
     * @param user The authenticated user attempting to update the review.
     * @param request The request payload with the updated review content.
     * @return A {@link ResponseEntity} containing the updated
     * {@link ReviewResponse}.
     */
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId, @AuthenticationPrincipal User user, @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ReviewResponse.fromEntity(
                        reviewService.updateReview(reviewId, request, user)
                )
        );
    }

}
