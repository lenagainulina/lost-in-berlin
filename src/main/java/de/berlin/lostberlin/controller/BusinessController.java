package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.service.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/businesses")
public class BusinessController {

    private BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping
    public ResponseEntity getBusinesses(@RequestParam (required=true, value = "location") String serviceLocation){
        return ResponseEntity.status(HttpStatus.OK).body(businessService.retrieveBusinessByLocation(serviceLocation));
    }

    @PostMapping
    public ResponseEntity createBusiness(@Valid @RequestBody BusinessPostDto businessProfile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.saveNewlyCreatedBusinessProfile(businessProfile));
    }

    @GetMapping("/{id}")
    public ResponseEntity getBusinessById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(businessService.retrieveBusinessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBusiness (@PathVariable(value = "id") Long id,
                           @Valid @RequestBody BusinessUpdateDto businessProfile) {

        return ResponseEntity.status(HttpStatus.OK).body(businessService.saveUpdatedBusinessProfile(id, businessProfile));
    }

}
