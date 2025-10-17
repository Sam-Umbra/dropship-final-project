package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Supplier;


@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String email);

    Optional<Supplier> findByEmail(String email);

    List<Supplier> findByNameIgnoreCaseContaining(String name);

}
