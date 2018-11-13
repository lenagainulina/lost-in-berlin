
package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.validation.constraints.NotNull;


@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT new de.berlin.lostberlin.model.order.client.OrderStatusDao (o.name, o.date, o.time, o.businessId, o.status)"+
    "from Order o where o.orderNr = :orderNr")
    OrderStatusDao getOrderStatus(@NotNull @Param("orderNr") String orderNr);

    @Query("SELECT new de.berlin.lostberlin.model.order.client.OrderFullDao (o.name, o.phone, o.eMail, o.date, o.time, o.participantsNr, o.description, o.businessId, o.status, o.orderNr)"+
    "from Order o where o.orderNr = :orderNr AND o.status = :status AND o.businessId = :businessId")
    OrderFullDao getFullOrder(@NotNull @Param("orderNr") String orderNr, @NotNull @Param("status") Enum status, @NotNull @Param("businessId") Long businessId);
}

