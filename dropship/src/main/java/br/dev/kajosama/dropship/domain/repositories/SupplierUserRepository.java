package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.SupplierUser;

/**
 * Repository interface for managing {@link SupplierUser} entities.
 * Provides methods for performing CRUD operations related to supplier-user associations.
 * @author Sam_Umbra
 */
@Repository
public interface SupplierUserRepository extends JpaRepository<SupplierUser, Long> {
 
}
