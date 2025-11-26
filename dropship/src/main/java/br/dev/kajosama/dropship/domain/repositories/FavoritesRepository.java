package br.dev.kajosama.dropship.domain.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.User;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    List<Favorites> findByUser(User user);

    Optional<Favorites> findByUserAndProductId(User user, Long productId);

    boolean existsByUserAndProductId(User user, Long productId);

    List<Favorites> findByUserAndProductIdIn(User user, Collection<Long> productIds);

}