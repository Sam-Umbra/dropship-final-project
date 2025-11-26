package br.dev.kajosama.dropship.domain.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.User;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Page<Favorites> findByUser(User user, Pageable pageable);

    Optional<Favorites> findByUserAndProductId(User user, Long productId);

    boolean existsByUserAndProductId(User user, Long productId);

}