package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Supplier;


/**
 * Repository interface for managing {@link Supplier} entities.
 * Provides methods for performing CRUD operations and custom queries related to suppliers.
 * @author Sam_Umbra
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
 * Checks if a supplier exists with the given email address.
 *
 * @param email The email address to check.
 * @return True if a supplier with the specified email exists, false otherwise.
 */
    boolean existsByEmail(String email);

    /**
 * Checks if a supplier exists with the given CNPJ.
 *
 * @param cnpj The CNPJ to check.
 * @return True if a supplier with the specified CNPJ exists, false otherwise.
 */
    boolean existsByCnpj(String cnpj);

    /**
 * Finds a supplier by its email address.
 *
 * @param email The email address of the supplier to find.
 * @return An {@link Optional} containing the found {@link Supplier} if it exists,
 * or an empty {@link Optional} if no supplier with the given email is found.
 */
    Optional<Supplier> findByEmail(String email);

    /**
 * Finds a list of suppliers whose names contain the given string, ignoring case.
 *
 * @param name The string to search for in supplier names.
 * @return A list of {@link Supplier} entities matching the search criteria.
 */
    List<Supplier> findByNameIgnoreCaseContaining(String name);

    /**
 * Finds a supplier by its ID and checks if it's approved.
 * @param id The ID of the supplier.
 * @param approved A boolean indicating the approval status to filter by.
 * @return An {@link Optional} containing the found {@link Supplier} if it exists and matches the approval status,
 * or an empty {@link Optional} otherwise.
 */
    Optional<Supplier> findByIdAndApproved(Long id, Boolean approved);

}
