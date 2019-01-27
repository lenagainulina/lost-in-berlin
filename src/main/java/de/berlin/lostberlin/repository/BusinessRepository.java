package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Query("SELECT new de.berlin.lostberlin.model.business.client.BusinessShortDao (b.id, b.fName, b.lName, b.description, b.serviceLocation, b.photo)"+
            "from Business b where b.serviceLocation = :serviceLocation")
    List<BusinessShortDao> getShortBusinessProfiles(@NotNull @Param("serviceLocation") String serviceLocation);


    boolean existsByEMail(String eMail);
}