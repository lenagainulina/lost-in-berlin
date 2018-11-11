package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.Business.Business;
import de.berlin.lostberlin.model.Business.BusinessShortDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Query("SELECT new de.berlin.lostberlin.model.Business.BusinessShortDao (b.fName, b.lName, b.description, b.serviceLocation, b.photo)"+
    "from Business b where b.serviceLocation = :serviceLocation")
    List<BusinessShortDao> findAllByServiceLocation(@Param("serviceLocation") String serviceLocation);
}
