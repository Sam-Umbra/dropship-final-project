package br.dev.kajosama.dropship.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Adiciona um produto aos favoritos de um usuário.
     *
     * @param user      O usuário que está favoritando o produto.
     * @param productId O ID do produto a ser favoritado.
     * @return O objeto Favorites salvo.
     * @throws EntityNotFoundException   Se o produto não for encontrado.
     * @throws IllegalStateException Se o produto já estiver nos favoritos.
     */
    @Transactional
    public Favorites addFavorite(User user, Long productId) {
        if (favoritesRepository.existsByUserAndProductId(user, productId)) {
            throw new IllegalStateException("Produto já está nos favoritos.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + productId + " não encontrado."));

        Favorites favorite = new Favorites();
        favorite.setUser(user);
        favorite.setProduct(product);

        return favoritesRepository.save(favorite);
    }

    /**
     * Remove um produto dos favoritos de um usuário.
     *
     * @param user      O usuário.
     * @param productId O ID do produto a ser removido.
     * @throws EntityNotFoundException Se o favorito não for encontrado.
     */
    @Transactional
    public void removeFavorite(User user, Long productId) {
        Favorites favorite = favoritesRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new EntityNotFoundException("Favorito não encontrado para este usuário e produto."));
        favoritesRepository.delete(favorite);
    }

    /**
     * Lista os produtos favoritados por um usuário com paginação.
     *
     * @param user     O usuário.
     * @param pageable As informações de paginação.
     * @return Uma página de favoritos.
     */
    public Page<Favorites> getFavorites(User user, Pageable pageable) {
        return favoritesRepository.findByUser(user, pageable);
    }
}