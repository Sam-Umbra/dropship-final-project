package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findByProductIdOrderByCreatedAtAsc(Long productId);

    List<Review> findByProductIdOrderByRatingDescCreatedAtDesc(Long productId);

    List<Review> findByProductIdOrderByRatingAscCreatedAtDesc(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
