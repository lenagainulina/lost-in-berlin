package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.EntityNotUniqueException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    private BusinessRepository businessRepo;

    @Autowired
    public BusinessServiceImpl(BusinessRepository businessRepo) {
        this.businessRepo = businessRepo;
    }

    @Override
    public List<BusinessShortDao> retrieveBusinessByLocation(String serviceLocation) {
        List<BusinessShortDao> businessShortDaoList = businessRepo.getShortBusinessProfiles(serviceLocation);
        if (businessShortDaoList.isEmpty()) {
            throw new ResourceNotFoundException("No matches for given location");
        }
        return businessShortDaoList;
    }

    @Override
    public Business saveNewlyCreatedBusinessProfile(BusinessPostDto businessProfile) {

        if (businessRepo.existsByEMail(businessProfile.getEMail())) {
            throw new EntityNotUniqueException("Email already exists");
        }
        Business business = new Business();
        business.setFName(businessProfile.getFName());
        business.setLName(businessProfile.getLName());
        business.setEMail(businessProfile.getEMail());
        business.setPhone(businessProfile.getPhone());
        business.setDescription(businessProfile.getDescription());
        business.setServiceLocation(businessProfile.getServiceLocation());
        business.setPhoto(businessProfile.getPhoto());
        business.setUsername(businessProfile.getUsername());
        business.setPassword(businessProfile.getPassword());

        return businessRepo.save(business);
    }

    @Override
    public Business retrieveBusinessById(Long id) {
        return businessRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found", "business profile", id));
    }

    @Override
    public Business saveUpdatedBusinessProfile(Long id, BusinessUpdateDto businessProfile) {
        Business business = businessRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found", "business profile", id));

        if (business.getEMail().equals(businessProfile.getEMail()) && businessRepo.existsByEMail(businessProfile.getEMail())) {
            throw new EntityNotUniqueException("Email already exists");
        }
        business.setFName(businessProfile.getFName());
        business.setLName(businessProfile.getLName());
        business.setEMail(businessProfile.getEMail());
        business.setPhone(businessProfile.getPhone());
        business.setDescription(businessProfile.getDescription());
        business.setServiceLocation(businessProfile.getServiceLocation());
        business.setPhoto(businessProfile.getPhoto());
        business.setUsername(businessProfile.getUsername());

        return businessRepo.save(business);
    }
}
