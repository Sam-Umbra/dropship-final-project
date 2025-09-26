package br.dev.kajosama.dropship.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductService {

    @Autowired
    ProductRepository productRepo;

    public boolean existsById(Long id) {
        return productRepo.existsById(id);
    }

    public Product getProductById(Long id) {
        return productRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product with ID: {" + id + "} NOT FOUND"));
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public void deleteProduct(Long id) {
        if(!existsById(id)) {
            throw new EntityNotFoundException("Product with ID: {" + id + "} NOT FOUND");
        }
        productRepo.deleteById(id);
    }

    /*
     * IMPLEMENTAR SEGURANÇA NA MANIPULAÇÃO DE PRODUTOS
     * SÓ DEVE SER PERMITIDO FAZER ALTERAÇÕES EM UM PRODUTO CASO O USUÁRIO
     * SEJA O FORNECEDOR DO PRODUTO
     */

}
