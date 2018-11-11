
package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.Order.Order;
import de.berlin.lostberlin.model.Order.OrderFullDao;
import de.berlin.lostberlin.model.Order.OrderStatusDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT new de.berlin.lostberlin.model.Order.OrderStatusDao(o.name, o.date, o.time, o.businessId, o.status)"+
    "from Order o where o.orderNr = :orderNr")
    OrderStatusDao findByOrderNr(@Param("orderNr") String orderNr);

    @Query("SELECT new de.berlin.lostberlin.model.Order.OrderFullDao(o.name, o.phone, o.eMail, o.date, o.time, o.participantsNr, o.description, o.businessId, o.status, o.orderNr)"+
    "from Order o where o.orderNr = :orderNr AND o.status = :status AND o.businessId = :businessId")
    OrderFullDao findByOrderNrAndStatusAndBusinessId(@Param("orderNr") String orderNr, @Param("status") Enum status, @Param("businessId") Long businessId);
}

