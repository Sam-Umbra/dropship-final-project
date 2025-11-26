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

@RestController
@RequestMapping("/user/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    /**
     * Adiciona um ou mais produtos aos favoritos do usuário autenticado.
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
     * @param user Usuário autenticado.
     * @param productIds IDs dos produtos a serem removidos dos favoritos,
     *                  recebe do parametro ?ids= na url
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
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getFavorites(
            @AuthenticationPrincipal User user) {
        List<ProductResponse> favoriteProducts = favoritesService.getFavorites(user)
                .stream().map(f -> ProductResponse.fromEntity(f.getProduct())).toList();
        return ResponseEntity.ok(favoriteProducts);
    }
}
