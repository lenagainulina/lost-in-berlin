package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.EntityNotUniqueException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BusinessServiceTest {

    @Autowired
    private BusinessRepository businessRepo;

    @Autowired
    private BusinessService businessService;

    @Before
    public void setUp() {
        businessService = new BusinessServiceImpl(businessRepo);
    }

    @Test
    public void retrieveBusinessByLocationTest() {
        String location = "Berlin";

        List<BusinessShortDao> business = businessRepo.getShortBusinessProfiles(location);

        assertFalse("List of businesses is empty", business.isEmpty());

        assertEquals("First name of the first object in the list is missing", "Ada", business.get(3).getFName());
        assertEquals("Last name of the first object in the list is missing", "Polkanova", business.get(3).getLName());
        assertEquals("Description of the first object in the list is missing", "Hi, I'm Ada, the dog", business.get(3).getDescription());
        assertEquals("Location of the first object in the list is missing", "berlin", business.get(3).getServiceLocation());
        assertEquals("Photo of the first object in the list is missing", null, business.get(3).getPhoto());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveBusinessByLocationBusinessNotFoundTest() {
        String location = "Paris";
        businessService.retrieveBusinessByLocation(location);
    }

    @Test
    public void saveNewlyCreatedBusinessProfileTest() {
        BusinessPostDto businessNew = mockBusinessNew();

        Business savedBusiness = businessService.saveNewlyCreatedBusinessProfile(businessNew);

        assertNotNull("business profile could not be saved", savedBusiness);

        assertEquals("First name couldn't be saved", "Ada", savedBusiness.getFName());
        assertEquals("Last name couldn't be saved", "Polkanova", savedBusiness.getLName());
        assertEquals("Email name couldn't be saved", "dogs4fun@dogmail.com", savedBusiness.getEMail());
        assertEquals("Phone name couldn't be saved", "017668558497", savedBusiness.getPhone());
        assertEquals("Description couldn't be saved", "Hi, I'm Ada, the dog", savedBusiness.getDescription());
        assertEquals("Location couldn't be saved", "berlin", savedBusiness.getServiceLocation());
        assertEquals("Username couldn't be saved", "polkaner", savedBusiness.getUsername());
    }

    @Test(expected = EntityNotUniqueException.class)
    public void saveNewlyCreatedBusinessProfileEmailAlreadyExistsTest() {
        BusinessPostDto businessNew = mockBusinessNew();
        businessService.saveNewlyCreatedBusinessProfile(businessNew);
    }

    @Test
    public void retrieveBusinessByIdTest() {
        Long id = 4L;
        Business retrievedBusiness = businessService.retrieveBusinessById(id);

        assertNotNull("No business profile with given id could be retrieved from the business repository", retrievedBusiness);

        assertEquals("First name is missing", "Ada", retrievedBusiness.getFName());
        assertEquals("Last name is missing", "Polkanova", retrievedBusiness.getLName());
        assertEquals("Email is missing", "dogs4fun@dogmail.com", retrievedBusiness.getEMail());
        assertEquals("Phone is missing", "017668558497", retrievedBusiness.getPhone());
        assertEquals("Description is missing", "Hi, I'm Ada, the dog", retrievedBusiness.getDescription());
        assertEquals("Location is missing", "berlin", retrievedBusiness.getServiceLocation());
        assertEquals("Username is missing", "polkaner", retrievedBusiness.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveBusinessByIdBusinessNotFoundTest() {
        Long id = 12L;
        businessService.retrieveBusinessById(id);
    }

    @Test
    public void saveUpdatedBusinessProfileTest() {
        Long id = 4L;

        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();

        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);

        assertNotNull("Updated business couldn't be saved", updBusiness);

        assertEquals("Updated first name couldn't be saved", "Adelaida", updBusiness.getFName());
        assertEquals("Updated last name couldn't be saved", "Polkanova", updBusiness.getLName());
        assertEquals("Updated email couldn't be saved", "polkan@dogsrgods.com", updBusiness.getEMail());
        assertEquals("Updated phone couldn't be saved", "017628834523", updBusiness.getPhone());
        assertEquals("Updated description couldn't be saved", "Hi, I'm Ada, the hairiest guide in Berlin", updBusiness.getDescription());
        assertEquals("Updated service location couldn't be saved", "Charlottenburg", updBusiness.getServiceLocation());
        assertEquals("Updated username couldn't be saved", "polkan", updBusiness.getUsername());
        assertEquals("Updated photo couldn't be saved", null, updBusiness.getPhoto()); //no logic for updating photo yet.
        //no logic for updating password yet. Perhaps, it should be a separate update procedure. It will be added after introducing general authentification logic.

    }

    @Test(expected = ResourceNotFoundException.class)
    public void saveUpdatedBusinessProfileNotFoundTest() {
        Long id = 12L;
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();
        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    @Test(expected = EntityNotUniqueException.class)
    public void saveUpdatedBusinessProfileEmailAlreadyExistsTest() {
        Long id = 4L;
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();

        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    private BusinessPostDto mockBusinessNew() {
        BusinessPostDto businessNew = new BusinessPostDto();
        businessNew.setFName("Ada");
        businessNew.setLName("Polkanova");
        businessNew.setEMail("dogs4fun@dogmail.com");
        businessNew.setPhone("017668558497");
        businessNew.setDescription("Hi, I'm Ada, the dog");
        businessNew.setServiceLocation("berlin");
        businessNew.setUsername("polkaner");
        businessNew.setPassword("bones");
        //    businessNew.setPhoto(null);
        return businessNew;
    }

    private BusinessUpdateDto mockBusinessUpdate() {
        BusinessUpdateDto businessUpdate = new BusinessUpdateDto();
        businessUpdate.setFName("Adelaida");
        businessUpdate.setLName("Polkanova");
        businessUpdate.setEMail("polkan@dogsrgods.com");
        businessUpdate.setPhone("017628834523");
        businessUpdate.setDescription("Hi, I'm Ada, the hairiest guide in Berlin");
        businessUpdate.setServiceLocation("Charlottenburg");
        businessUpdate.setUsername("polkan");
        businessUpdate.setPhoto(null);

        return businessUpdate;
    }

}