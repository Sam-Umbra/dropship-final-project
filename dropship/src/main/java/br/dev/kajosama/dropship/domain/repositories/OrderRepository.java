package br.dev.kajosama.dropship.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Order;
import br.dev.kajosama.dropship.domain.model.enums.OrderStatus;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);
    boolean existsByUserIdAndStatus(Long userId, OrderStatus status);
    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

}
