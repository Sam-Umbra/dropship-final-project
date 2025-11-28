package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Product;

/**
 * Repository interface for managing {@link Product} entities.
 * Provides methods for performing CRUD operations and custom queries related to products.
 * @author Sam_Umbra
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
 * Finds a product by its name.
 *
 * @param name The name of the product to find.
 * @return An {@link Optional} containing the found {@link Product} if it exists,
 * or an empty {@link Optional} if no product with the given name is found.
 */
    Optional<Product> findByName(String name);

    /**
 * Finds a list of products whose names contain the given string, ignoring case.
 *
 * @param name The string to search for in product names.
 * @return A list of {@link Product} entities matching the search criteria.
 */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
 * Finds a list of products belonging to a specific category.
 *
 * @param categoryId The ID of the category.
 * @return A list of {@link Product} entities associated with the given category ID.
 */
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(Long categoryId);

    /**
 * Finds a list of products provided by a specific supplier.
 *
 * @param supplierId The ID of the supplier.
 * @return A list of {@link Product} entities provided by the given supplier ID.
 */
    @Query("SELECT p FROM Product p WHERE p.supplier.id = :supplierId")
    List<Product> findBySupplierId(Long supplierId);

    /**
 * Finds a list of products where the supplier's name contains the given string, ignoring case.
 *
 * @param supplierName The string to search for in supplier names.
 * @return A list of {@link Product} entities whose supplier's name matches the search criteria.
 */
    @Query("""
       SELECT p 
       FROM Product p 
       WHERE LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :supplierName, '%'))
       """)
    List<Product> findBySupplierNameContainingIgnoreCase(String supplierName);

}
