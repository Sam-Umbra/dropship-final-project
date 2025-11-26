package br.dev.kajosama.dropship.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.kajosama.dropship.api.services.FavoritesService;
import br.dev.kajosama.dropship.domain.model.entities.Favorites;
import br.dev.kajosama.dropship.domain.model.entities.User;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    /**
     * Adiciona um produto aos favoritos do usuário autenticado.
     */
    @PostMapping("/{productId}")
    public ResponseEntity<Favorites> addFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        Favorites favorite = favoritesService.addFavorite(user, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    /**
     * Remove um produto dos favoritos do usuário autenticado.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        favoritesService.removeFavorite(user, productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista os produtos favoritos do usuário autenticado.
     */
    @GetMapping
    public ResponseEntity<Page<Favorites>> getFavorites(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(favoritesService.getFavorites(user, pageable));
    }
}