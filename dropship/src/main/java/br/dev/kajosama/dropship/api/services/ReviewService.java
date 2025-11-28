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

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    public List<Review> getReviewsForProduct(Long productId, ReviewSortBy sortBy) {
        productService.getProductById(productId);

        return switch (sortBy) {
            case NEWEST -> reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
            case OLDEST -> reviewRepository.findByProductIdOrderByCreatedAtAsc(productId);
            case HIGHEST_RATING -> reviewRepository.findByProductIdOrderByRatingDescCreatedAtDesc(productId);
            case LOWEST_RATING -> reviewRepository.findByProductIdOrderByRatingAscCreatedAtDesc(productId);
            default -> reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        };
    }

    private Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

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

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = getReviewById(reviewId);

        reviewUserMatchesCurrentUser(review, user, "delete");

        reviewRepository.delete(review);

    }

    @Transactional
    public Review updateReview(Long reviewId, ReviewUpdateRequest request, User user) {
        Review review = getReviewById(reviewId);

        reviewUserMatchesCurrentUser(review, user, "update");

        if(!request.comment().isBlank()) {
            review.setComment(request.comment());
        }

        if(request.rating() != null) {
            review.setRating(request.rating());
        }

        if(request.imageUrls() != null) {
            review.setImageUrls(request.imageUrls());
        }

        return saveReview(review);

    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));
    }

    private void reviewUserMatchesCurrentUser(Review review, User user, String msg) {
        if (!review.getUser().getId().equals(user.getId()) && !user.hasRole("ADMIN")) {
            throw new AccessDeniedException("You can't " + msg + " a review that doesn't belong to you unless you are an ADMIN");
        }

    }

}
