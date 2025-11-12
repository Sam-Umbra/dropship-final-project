package br.dev.kajosama.dropship.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.dev.kajosama.dropship.domain.model.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
