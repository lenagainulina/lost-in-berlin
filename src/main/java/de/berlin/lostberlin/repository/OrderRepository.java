package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //Order findByOrderNr(String orderNr);
}
