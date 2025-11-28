package br.dev.kajosama.dropship.api.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.repositories.FavoritesRepository;
import br.dev.kajosama.dropship.domain.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Favorites} entities.
 *              Provides business logic for adding, removing, and retrieving
 *              user favorite products.
 *              It interacts with {@link FavoritesRepository} and
 *              {@link ProductRepository}.
 */
@Service
public class FavoritesService {

    /**
     * Repository for {@link Favorites} entities.
     */
    @Autowired
    private FavoritesRepository favoritesRepository;

    /**
     * Repository for {@link Product} entities.
     */
    @Autowired
    private ProductRepository productRepository;

    /**
     * Adds a list of products to a user's favorites.
     * This method is idempotent, meaning it will not add a product to favorites
     * if it's already there for the given user.
     *
     * @param user       The {@link User} who is adding products to their favorites.
     * @param productIds A {@link List} of product IDs to be favorited.
     * @return A {@link List} of {@link Favorites} objects that were newly saved.
     *         Returns an empty list if {@code productIds} is null or empty, or if
     *         all
     *         specified products are already favorited by the user.
     * @throws EntityNotFoundException If any product specified in
     *                                 {@code productIds}
     *                                 is not found in the database.
     */
    @Transactional
    public List<Favorites> addFavorites(User user, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Favorites> existingFavorites = favoritesRepository.findByUserAndProductIdIn(user, productIds);
        var existingProductIds = new HashSet<Long>();
        for (Favorites f : existingFavorites) {
            existingProductIds.add(f.getProduct().getId());
        }

        var newProductIds = new ArrayList<Long>();
        for (Long id : productIds) {
            if (!existingProductIds.contains(id)) {
                newProductIds.add(id);
            }
        }

        if (newProductIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> productsToAdd = productRepository.findAllById(newProductIds);

        List<Favorites> newFavorites = productsToAdd.stream().map(product -> new Favorites(user, product)).toList();
        return favoritesRepository.saveAll(newFavorites);
    }

    /**
     * Removes products from a user's favorites.
     * This operation is idempotent: it will not fail if a favorite entry
     * for a given product and user does not exist.
     *
     * @param user       The {@link User} whose favorites are to be modified.
     * @param productIds A {@link Set} of product IDs to be removed from favorites.
     *                   If {@code productIds} is null or empty, the method does
     *                   nothing.
     */
    @Transactional
    public void removeFavorites(User user, Set<Long> productIds) {
        // 1. Fail-fast para entradas inválidas
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        // 2. Busca os itens existentes
        List<Favorites> favorites = favoritesRepository
                .findByUserAndProductIdIn(user, productIds);

        if (!favorites.isEmpty()) {
            favoritesRepository.deleteAllInBatch(favorites);
        }
    }

    /**
     * Retrieves a list of all favorite products for a given user.
     *
     * @param user The {@link User} whose favorite products are to be retrieved.
     * @return A {@link List} of {@link Favorites} entities belonging to the
     *         specified user.
     *         Returns an empty list if the user has no favorite products.
     */
    public List<Favorites> getFavorites(User user) {
        return favoritesRepository.findByUser(user);
    }
}
