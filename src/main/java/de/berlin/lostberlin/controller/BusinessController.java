package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.client.BusinessUpdatePhotoDto;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.service.BusinessService;
import de.berlin.lostberlin.service.fileUpload.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private FileService fileService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping
    public ResponseEntity<List<BusinessShortDao>> getBusinesses(@RequestParam(required = true, value = "location") String serviceLocation) {
        return ResponseEntity.status(HttpStatus.OK).body(businessService.retrieveBusinessByLocation(serviceLocation));
    }

    @PostMapping
    public ResponseEntity<Business> createBusiness(@Valid @RequestBody BusinessPostDto businessProfile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.saveNewlyCreatedBusinessProfile(businessProfile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Business> getBusinessById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(businessService.retrieveBusinessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Business> updateBusinessFully(@PathVariable(value = "id") Long id,
                                                        @Valid @RequestBody BusinessUpdateDto businessProfile) {

        return ResponseEntity.status(HttpStatus.OK).body(businessService.saveUpdatedBusinessProfile(id, businessProfile));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Business> updateBusinessPartially(@PathVariable(value = "id") Long id,
                                                            @Valid @RequestBody BusinessUpdatePhotoDto photoFilePath){
        return ResponseEntity.status(HttpStatus.OK).body(businessService.savePartiallyUpdatedBusinessProfile(id, photoFilePath));
    }

    @PostMapping("/{id}/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.storeFile(file);
        return ResponseEntity.ok().body("You successfully uploaded " + file.getOriginalFilename() + "!");
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = fileService.loadFileAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
