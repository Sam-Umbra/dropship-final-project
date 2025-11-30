package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.Set;

import br.dev.kajosama.dropship.domain.model.entities.Review;

/**
 * Represents the data transfer object for a product review.
 *
 * @author Sam_Umbra
 * @param reviewId The unique identifier for the review.
 * @param rating The rating given, from 0 to 5.
 * @param comment The text content of the review.
 * @param reviewDate The date and time the review was created, as a string.
 * @param reviewerName The name of the user who wrote the review.
 * @param reviewerId The ID of the user who wrote the review.
 * @param imageUrls A set of URLs for images attached to the review.
 */
public record ReviewResponse(
        Long reviewId,
        Integer rating,
        String comment,
        /**
         * The date and time the review was created, as a string.
         */
        String reviewDate,
        /**
         * The name of the user who wrote the review.
         */
        String reviewerName,
        /**
         * The ID of the user who wrote the review.
         */
        Long reviewerId,
        Set<String> imageUrls
        ) {

    /**
     * Creates a {@link ReviewResponse} from a {@link Review} entity.
     *
     * @param review The {@link Review} entity to convert.
     * @return A new {@link ReviewResponse} object.
     */
    public static ReviewResponse fromEntity(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt().toString(),
                review.getUser().getName(),
                review.getUser().getId(),
                review.getImageUrls()
        );
    }
}
