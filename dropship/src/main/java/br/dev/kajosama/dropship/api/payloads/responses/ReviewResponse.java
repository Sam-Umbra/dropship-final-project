package br.dev.kajosama.dropship.api.payloads.responses;

import java.util.Set;

import br.dev.kajosama.dropship.domain.model.entities.Review;

public record ReviewResponse(
    Long reviewId,
    Integer rating,
    String comment,
    String reviewDate,
    String reviewerName,
    Long reviewerId,
    Set<String> imageUrls
) {
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
