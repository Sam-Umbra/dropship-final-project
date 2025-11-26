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

@Service
public class FavoritesService {

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Adiciona uma lista de produtos aos favoritos de um usuário.
     *
     * @param user O usuário que está favoritando o produto.
     * @param productIds A lista de IDs de produtos a serem favoritados.
     * @return A lista de objetos Favorites salvos.
     * @throws EntityNotFoundException Se o produto não for encontrado.
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
     * Remove produtos dos favoritos de um usuário. Operação idempotente: não
     * falha se o favorito já não existir.
     *
     * @param user O usuário.
     * @param productIds Os IDs dos produtos a serem removidos.
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
     * Lista os produtos favoritados por um usuário.
     *
     * @param user O usuário.
     * @return Uma lista de favoritos.
     */
    public List<Favorites> getFavorites(User user) {
        return favoritesRepository.findByUser(user);
    }
}
