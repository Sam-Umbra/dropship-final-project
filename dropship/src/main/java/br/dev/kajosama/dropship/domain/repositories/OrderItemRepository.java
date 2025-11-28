package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.OrderItem;

/**
 * Repository interface for managing {@link OrderItem} entities.
 * Provides methods for performing CRUD operations related to order items.
 * @author Sam_Umbra
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
