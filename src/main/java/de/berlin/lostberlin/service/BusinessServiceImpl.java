package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.EntityNotUniqueException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.client.BusinessUpdatePhotoDto;
import de.berlin.lostberlin.model.business.persistence.Business;
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
        updateNotEmptyFields(businessProfile, business);

        return businessRepo.save(business);
    }

    @Override
    public Business savePartiallyUpdatedBusinessProfile(Long id, BusinessUpdatePhotoDto photoFilePath) {
        Business business = businessRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found", "business profile", id));
        if(photoFilePath!=null){
            business.setPhoto(photoFilePath.getPhoto());
        }
        return businessRepo.save(business);
    }

    private void updateNotEmptyFields (BusinessUpdateDto businessProfile, Business business){

        if(businessProfile.getFName()!=null){
            business.setFName(businessProfile.getFName());}

        if(businessProfile.getLName()!=null){
            business.setLName(businessProfile.getLName());}

        if(businessProfile.getEMail()!=null){
            business.setEMail(businessProfile.getEMail());}

        if(businessProfile.getPhone()!=null){
            business.setPhone(businessProfile.getPhone());}

        if(businessProfile.getDescription()!=null){
            business.setDescription(businessProfile.getDescription());}

        if(businessProfile.getServiceLocation()!=null){
            business.setServiceLocation(businessProfile.getServiceLocation());}

        if(businessProfile.getPhoto()!=null){
            business.setPhoto(businessProfile.getPhoto());}

        if(businessProfile.getUsername()!=null){
            business.setUsername(businessProfile.getUsername());}
    }
}
