package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;

/**
 * Repository interface for managing {@link Order} entities.
 * Provides methods for performing CRUD operations and custom queries related to orders.
 * @author Sam_Umbra
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
 * Finds all orders associated with a specific user ID.
 *
 * @param userId The ID of the user whose orders are to be found.
 * @return A list of {@link Order} entities belonging to the specified user.
 */
    List<Order> findAllByUserId(Long userId);

    /**
 * Checks if an order exists for a given user with a specific status.
 *
 * @param userId The ID of the user to check.
 * @param status The {@link OrderStatus} to check for.
 * @return True if an order exists for the user with the specified status, false otherwise.
 */
    boolean existsByUserIdAndStatus(Long userId, OrderStatus status);

    /**
 * Finds a specific order for a user with a given status.
 * This is typically used to find an active shopping cart (e.g., PENDING status).
 *
 * @param userId The ID of the user whose order is to be found.
 * @param status The {@link OrderStatus} of the order to find.
 * @return An {@link Optional} containing the found {@link Order} if it exists,
 * or an empty {@link Optional} if no such order is found.
 */
    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

}
