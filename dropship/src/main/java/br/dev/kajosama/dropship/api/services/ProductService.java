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

/**
 * @author Sam_Umbra
 * @Description Service class for managing {@link Product} entities.
 *              Provides business logic for product-related operations such as
 *              retrieving, registering, updating, and deleting products. It
 *              interacts with
 *              {@link ProductRepository}, {@link SupplierRepository}, and
 *              {@link CategoryRepository}.
 */
@Service
@Transactional
public class ProductService {

    /**
     * Repository for {@link Product} entities.
     */
    @Autowired
    ProductRepository productRepo;

    /**
     * Repository for {@link Supplier} entities.
     */
    @Autowired
    SupplierRepository supplierRepo;

    /**
     * Repository for {@link Category} entities.
     */
    @Autowired
    CategoryRepository categoryRepo;

    /**
     * Checks if a product exists by its ID.
     *
     * @param id The ID of the product to check.
     * @return True if a product with the specified ID exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return productRepo.existsById(id);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The {@link Product} entity with the specified ID.
     * @throws EntityNotFoundException If the product with the given ID is not
     *                                 found.
     */
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: {" + id + "} NOT FOUND"));
    }

    /**
     * Retrieves a list of all products.
     *
     * @return A {@link List} of all {@link Product} entities.
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * Saves a {@link Product} entity to the database.
     *
     * @param product The {@link Product} entity to save.
     * @return The saved {@link Product} entity.
     */
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    /**
     * Registers a new product.
     * This involves associating the product with an existing {@link Supplier} and
     * one or more {@link Category} entities.
     * The product's status is set to ACTIVE upon registration.
     *
     * @param request The {@link ProductRegisterRequest} containing details for the
     *                new product.
     * @return The newly registered {@link Product} entity.
     * @throws EntityNotFoundException If the specified supplier or any of the
     *                                 categories are not found.
     * @throws AccessDeniedException   If the supplier associated with the product
     *                                 is not active.
     */
    public Product registerProduct(ProductRegisterRequest request) {

        /**
         * Retrieves the supplier for the product.
         *
         * @param request The {@link ProductRegisterRequest} containing the supplier ID.
         * @return The {@link Supplier} entity.
         * @throws EntityNotFoundException If the supplier with the given ID is not
         *                                 found.
         */
        Supplier supplier = supplierRepo.findById(request.supplierId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Supplier with the id: {" + request.supplierId() + "} NOT FOUND"));

        if (!isSupplierActive(supplier)) {
            throw new AccessDeniedException(String.format(
                    "Supplier '%s' cannot register a product unless they have an ACTIVE account",
                    supplier.getName()));
        }

        /**
         * Retrieves the categories for the product.
         *
         * @param request The {@link ProductRegisterRequest} containing the category
         *                IDs.
         * @throws EntityNotFoundException If any of the categories with the given IDs
         *                                 are not found.
         */
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

        /**
         * Sets the initial status of the product to ACTIVE.
         */
        product.setStatus(ProductStatus.ACTIVE);
        /**
         * Sets the supplier for the product.
         */
        product.setSupplier(supplier);
        /**
         * Sets the categories for the product.
         */
        product.setCategories(new HashSet<>(categories));

        return saveProduct(product);
    }

    /**
     * Soft deletes a product by setting its status to INACTIVE.
     * This operation requires the current user to be either an ADMIN or a user
     * associated with the product's supplier,
     * and the supplier must be active.
     *
     * @param id The ID of the product to delete.
     * @throws EntityNotFoundException If the product with the given ID is not
     *                                 found.
     * @throws AccessDeniedException   If the current user does not have permission
     *                                 to delete the product,
     *                                 or if the supplier is not active.
     */
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id: {" + id + "} NOT FOUND"));

        verifyIfSupplierMatcherUserAndProduct(product);

        /**
         * Sets the product status to INACTIVE.
         */
        product.setStatus(ProductStatus.INACTIVE);
        saveProduct(product);
    }

    /**
     * Retrieves a list of products belonging to a specific category.
     *
     * @param id The ID of the category.
     * @return A {@link List} of {@link Product} entities associated with the given
     *         category ID.
     */
    public List<Product> getProductByCategoryId(Long id) {
        return productRepo.findByCategoryId(id);
    }

    /**
     * Retrieves a list of products whose names contain the given string
     * (case-insensitive).
     *
     * @param name The string to search for in product names.
     * @return A {@link List} of {@link Product} entities matching the search
     *         criteria.
     */
    public List<Product> getProductsByName(String name) {
        return productRepo.findByNameContainingIgnoreCase(name);
    }

    /**
     * Retrieves a list of products provided by a specific supplier.
     *
     * @param id The ID of the supplier.
     * @return A {@link List} of {@link Product} entities provided by the given
     *         supplier ID.
     */
    public List<Product> getProductsBySupplierId(Long id) {
        return productRepo.findBySupplierId(id);
    }

    /**
     * Retrieves a list of products where the supplier's name contains the given
     * string (case-insensitive).
     *
     * @param name The string to search for in supplier names.
     * @return A {@link List} of {@link Product} entities whose supplier's name
     *         matches the search criteria.
     */
    public List<Product> getProductsBySupplierName(String name) {
        return productRepo.findBySupplierNameContainingIgnoreCase(name);
    }

    /**
     * Verifies if the current authenticated user has permission to modify a given
     * product.
     * Permission is granted if the current user is an ADMIN or a user associated
     * with the product's supplier,
     * and the supplier's account is active.
     *
     * @param product The {@link Product} to check permissions for.
     * @throws AccessDeniedException   If the current user does not have the
     *                                 necessary permissions.
     * @throws EntityNotFoundException If the product does not have an associated
     *                                 supplier.
     */
    public void verifyIfSupplierMatcherUserAndProduct(Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof User currentUser)) {
            throw new AccessDeniedException("User not found or invalid token");
        }

        /**
         * Retrieves the supplier associated with the product.
         *
         * @param product The {@link Product} entity.
         * @return The {@link Supplier} entity.
         * @throws EntityNotFoundException If the product does not have an associated
         *                                 supplier.
         */
        Supplier supplier = product.getSupplier();
        if (supplier == null) {
            throw new EntityNotFoundException("There is no supplier for the product");
        }

        /**
         * Checks if the current user is a user associated with the product's supplier
         * and if the supplier is active.
         *
         * @param supplier    The {@link Supplier} entity.
         * @param currentUser The currently authenticated {@link User}.
         * @return True if the current user is a supplier user and the supplier is
         *         active, false otherwise.
         */
        boolean isSupplierUser = isSupplierActive(supplier)
                && Optional.ofNullable(supplier.getSupplierUsers())
                        .orElse(Collections.emptyList())
                        .stream()
                        .anyMatch(su -> su.getUser().getId().equals(currentUser.getId()));

        /**
         * Throws an {@link AccessDeniedException} if the current user does not have
         * permission to modify the product.
         *
         * @param isSupplierUser True if the current user is a supplier user, false
         *                       otherwise.
         * @param currentUser    The currently authenticated {@link User}.
         * @param product        The {@link Product} entity.
         * @param supplier       The {@link Supplier} entity.
         * @throws AccessDeniedException If the current user does not have permission.
         */
        if (!isSupplierUser && !currentUser.hasRole("ADMIN")) {
            throw new AccessDeniedException(String.format(
                    "User '%s' cannot modify product '%s' from supplier '%s' unless they are an ADMIN and you must have an ACTIVE account",
                    currentUser.getName(),
                    product.getName(),
                    supplier.getName()));
        }
    }

    /**
     * Checks if a given supplier's account status is ACTIVE.
     *
     * @param supplier The {@link Supplier} entity to check.
     * @return True if the supplier's status is ACTIVE, false otherwise.
     */
    public boolean isSupplierActive(Supplier supplier) {
        return supplier.getStatus().equals(AccountStatus.ACTIVE);
    }
}
