package br.dev.kajosama.dropship.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.entities.Supplier;
import br.dev.kajosama.dropship.domain.model.enums.AccountStatus;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String email);

    Optional<Supplier> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Supplier u SET u.deletedAt = CURRENT_TIMESTAMP, u.status = 'DELETED' WHERE u.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Supplier u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateStatus(@Param("status") AccountStatus status, @Param("id") Long id);

}
