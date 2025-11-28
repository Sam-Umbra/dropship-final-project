package br.dev.kajosama.dropship.api.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.payloads.responses.ProductResponse;
import br.dev.kajosama.dropship.api.services.FavoritesService;
import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.User;

/**
 * REST controller for managing a user's favorite products. Provides endpoints
 * for adding, removing, and listing favorite products.
 *
 * @author Sam_Umbra
 */
@RestController
@RequestMapping("/user/favorites")
public class FavoritesController {

    /**
     * Service for handling business logic related to user favorites.
     */
    @Autowired
    private FavoritesService favoritesService;

    /**
     * Adiciona um ou mais produtos aos favoritos do usuário autenticado.
     *
     * @param user The authenticated user, injected by Spring Security.
     * @param productIds A list of product IDs to add to the favorites.
     * @return A {@link ResponseEntity} containing the list of products that
     * were added to favorites, with a 201 Created status.
     */
    @PostMapping
    public ResponseEntity<List<ProductResponse>> addFavorites(
            @AuthenticationPrincipal User user,
            @RequestBody List<Long> productIds) {
        List<Favorites> favorites = favoritesService.addFavorites(user, productIds);

        if (favorites.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<ProductResponse> favoriteProducts = favorites.stream()
                .map(f -> ProductResponse.fromEntity(f.getProduct()))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteProducts);
    }

    /**
     * Remove produtos dos favoritos do usuário autenticado.
     *
     * @param user The authenticated user, injected by Spring Security.
     * @param productIds A set of product IDs to be removed from favorites,
     * received from the "?ids=" URL parameter.
     * @return A {@link ResponseEntity} with no content (204).
     */
    @DeleteMapping
    public ResponseEntity<Void> removeFavorites(
            @AuthenticationPrincipal User user,
            @RequestParam("ids") Set<Long> productIds) { // Mudou para @RequestParam e Set interface

        favoritesService.removeFavorites(user, productIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista os produtos favoritos do usuário autenticado.
     *
     * @param user The authenticated user, injected by Spring Security.
     * @return A {@link ResponseEntity} containing a list of the user's favorite
     * products.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getFavorites(
            @AuthenticationPrincipal User user) {
        List<ProductResponse> favoriteProducts = favoritesService.getFavorites(user)
                .stream().map(f -> ProductResponse.fromEntity(f.getProduct())).toList();
        return ResponseEntity.ok(favoriteProducts);
    }
}
