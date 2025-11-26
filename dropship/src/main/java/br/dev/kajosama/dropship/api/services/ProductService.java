package br.dev.kajosama.dropship.api.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.api.payloads.requests.ProductRegisterRequest;
import br.dev.kajosama.dropship.domain.model.entities.Category;
import br.dev.kajosama.dropship.domain.model.entities.Product;
import br.dev.kajosama.dropship.domain.model.entities.Supplier;
import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;
import br.dev.kajosama.dropship.domain.model.enums.ProductStatus;
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

        Supplier supplier = supplierRepo.findById(request.supplierId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Supplier with the id: {" + request.supplierId() + "} NOT FOUND"));
        
        if (!isSupplierActive(supplier)) {
            throw new AccessDeniedException(String.format(
                "Supplier '%s' cannot register a product unless they have an ACTIVE account",
                supplier.getName()
            ));
        }

        List<Category> categories = categoryRepo.findAllById(request.categoryIds());
        if (categories.isEmpty()) {
            throw new EntityNotFoundException("No categories with the ids: {" + request.categoryIds() + "} found");
        }

        Product product = new Product(
                request.name(),
                request.description(),
                new Price(request.price()),
                request.stock(),
                request.imgUrl(),
                request.discount());

        product.setStatus(ProductStatus.ACTIVE);
        product.setSupplier(supplier);
        product.setCategories(new HashSet<>(categories));

        return saveProduct(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id: {" + id + "} NOT FOUND"));

        verifyIfSupplierMatcherUserAndProduct(product);

        product.setStatus(ProductStatus.INACTIVE);
        saveProduct(product);
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

    public void verifyIfSupplierMatcherUserAndProduct(Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("User not found or invalid token");
        }

        Supplier supplier = product.getSupplier();
        if (supplier == null) {
            throw new EntityNotFoundException("There is no supplier for the product");
        }

        boolean isSupplierUser = isSupplierActive(supplier)
                && Optional.ofNullable(supplier.getSupplierUsers())
                        .orElse(Collections.emptyList())
                        .stream()
                        .anyMatch(su -> su.getUser().getId().equals(currentUser.getId()));

        if (!isSupplierUser && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException(String.format(
                    "User '%s' cannot modify product '%s' from supplier '%s' unless they are an ADMIN and you must have an ACTIVE account",
                    currentUser.getName(),
                    product.getName(),
                    supplier.getName()));
        }
    }

    public boolean isSupplierActive(Supplier supplier) {
        return supplier.getStatus().equals(AccountStatus.ACTIVE);
    }
}
