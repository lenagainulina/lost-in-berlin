package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.Business;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class BusinessController {
    @Autowired
    private BusinessRepository businessRepo;


    @GetMapping("/businesses")
    public List<Business> getBusinesses(){
       return businessRepo.findAll();
    }

    @PostMapping("/businesses")
    public Business createBusiness(@Valid @RequestBody Business business) {
        return businessRepo.save(business);
    }

    @GetMapping("/businesses/{id}")
    public Business getBusinessById(@PathVariable(value = "id") Long id) {
        return businessRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business", "id", id));
    }

    @PutMapping("/businesses/{id}")
    public Business updateBusiness (@PathVariable(value = "id") Long id,
                           @Valid @RequestBody Business businessProfile) {

        Business business = businessRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business", "id", id));

        business.setfName(businessProfile.getfName());
        business.setlName(businessProfile.getlName());
        business.seteMail(businessProfile.geteMail());
        business.setPhone(businessProfile.getPhone());
        business.setDescription(businessProfile.getDescription());
        business.setPhoto(businessProfile.getPhoto());

        Business updatedBusiness = businessRepo.save(business);
        return updatedBusiness;
    }

}
