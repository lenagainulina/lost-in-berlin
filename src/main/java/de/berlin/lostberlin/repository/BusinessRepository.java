package de.berlin.lostberlin.repository;

import de.berlin.lostberlin.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findAllByServiceLocation(String serviceLocation);
}
