
package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByBusinessId (Long businessId);
}

