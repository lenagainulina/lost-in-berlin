package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.order.persistence.ChosenBusiness;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChosenBusinessRepository extends JpaRepository<ChosenBusiness, Long> {
    @Query("SELECT new de.berlin.lostberlin.model.order.client.OrderShortDao (o.name, o.date, o.time, o.participantsNr, o.description)" +
            "from ChosenBusiness cb inner join Order o on cb.orderNr = o.orderNr where cb.businessId = :businessId")
    List<OrderShortDao> findAllByBusinessId (@Param("businessId") Long businessId);
}