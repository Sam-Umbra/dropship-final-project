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

@RestController
@RequestMapping("/product")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProduct(@PathVariable Long productId,
            @RequestParam(name = "sort", defaultValue = "NEWEST") ReviewSortBy sortBy) {
        List<ReviewResponse> reviews = reviewService.getReviewsForProduct(productId, sortBy).stream()
                .map(ReviewResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewResponse> saveReview(@PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal User user) {
        Review savedReview = reviewService.addReview(productId, user, request);
        ReviewResponse response = ReviewResponse.fromEntity(savedReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long reviewId, @AuthenticationPrincipal User user, @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ReviewResponse.fromEntity(
                reviewService.updateReview(reviewId, request, user)
            )
        );
    }

}
