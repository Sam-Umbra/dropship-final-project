package br.dev.kajosama.dropship.api.services;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.payloads.requests.ProductRegisterRequest;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.objects.Price;
import br.dev.kajosama.dropship.domain.repositories.CategoryRepository;
import br.dev.kajosama.dropship.domain.repositories.ProductRepository;
import br.dev.kajosama.dropship.domain.repositories.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductService {

    @Autowired
    ProductRepository productRepo;

    @Autowired
    SupplierRepository supplierRepo;

    @Autowired
    CategoryRepository categoryRepo;

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

    public Product registerProduct(ProductRegisterRequest request) {
        var supplier = supplierRepo.findById(request.supplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier with the id: {" + request.supplierId() + "} NOT FOUND"));

        var categories = categoryRepo.findAllById(request.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("No categories with the ids: {" + request.categoryIds() + "} found");
        }

        Product product = new Product(
                request.name(),
                request.description(),
                new Price(request.price()),
                request.stock(),
                request.status(),
                request.imgUrl(),
                request.discount()
        );

        product.setSupplier(supplier);
        product.setCategories(new HashSet<>(categories));

        return saveProduct(product);
    }

    public void deleteProduct(Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Product with ID: {" + id + "} NOT FOUND");
        }
        productRepo.deleteById(id);
    }

    public List<Product> getProductByCategoryId(Long id) {
        return productRepo.findByCategoryId(id);
    }

    public List<Product> getProductsByName(String name) {
        return productRepo.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsBySupplierId(Long id) {
        return productRepo.findBySupplierId(id);
    }

    public List<Product> getProductsBySupplierName(String name) {
        return productRepo.findBySupplierNameContainingIgnoreCase(name);
    }

    /*
     * IMPLEMENTAR SEGURANÇA NA MANIPULAÇÃO DE PRODUTOS
     * SÓ DEVE SER PERMITIDO FAZER ALTERAÇÕES EM UM PRODUTO CASO O USUÁRIO
     * SEJA O FORNECEDOR DO PRODUTO
     */
}
