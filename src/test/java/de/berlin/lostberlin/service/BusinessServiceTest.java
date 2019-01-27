package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.EntityNotUniqueException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.client.BusinessUpdatePhotoDto;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BusinessServiceTest {

    @TestConfiguration
    @EnableJpaAuditing
    public static class BusinessServiceTestConfiguration {
        @Autowired
        private BusinessRepository businessRepository;

        @Bean
        public BusinessService createBusinessService() {
            return new BusinessServiceImpl(businessRepository);
        }
    }

    @Autowired
    private BusinessService businessService;

    @Autowired
    private TestEntityManager entityManager;

    private Business testBusiness;

    @Before
    public void setUp() {
        testBusiness = mockBusiness();
        entityManager.persist(testBusiness);
    }


    @Test
    public void retrieveBusinessByLocationTest() {
        String location = "Berlin";
        List<BusinessShortDao> business = businessService.retrieveBusinessByLocation(location);

        assertFalse("List of businesses is empty", business.isEmpty());

        assertEquals("First name of the first object in the list doesn't match", "Ada", business.get(0).getFName());
        assertEquals("Last name of the first object in the list doesn't match", "Polkanova", business.get(0).getLName());
        assertEquals("Description of the first object in the list doesn't match", "Hi, I'm Ada, the dog", business.get(0).getDescription());
        assertEquals("Location of the first object in the list doesn't match", "Berlin", business.get(0).getServiceLocation());
        assertEquals("Photo of the first object in the list doesn't match", null, business.get(0).getPhoto());
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

        assertEquals("First name doesn't match", "Ada", savedBusiness.getFName());
        assertEquals("Last name doesn't match", "Polkanova", savedBusiness.getLName());
        assertEquals("Email name doesn't match", "dogsledging@doggy.de", savedBusiness.getEMail());
        assertEquals("Phone name doesn't match", "017668558497", savedBusiness.getPhone());
        assertEquals("Description doesn't match", "Hi, I'm Ada, the dog", savedBusiness.getDescription());
        assertEquals("Location doesn't match", "berlin", savedBusiness.getServiceLocation());
        assertEquals("Username doesn't match", "polkaner", savedBusiness.getUsername());
    }

    @Test(expected = EntityNotUniqueException.class)
    public void saveNewlyCreatedBusinessProfileEmailAlreadyExistsTest() {
        BusinessPostDto businessNew = mockBusinessNew();
        Business savedBusiness1 = businessService.saveNewlyCreatedBusinessProfile(businessNew);
        Business savedBusiness2 = businessService.saveNewlyCreatedBusinessProfile(businessNew);
    }

    @Test
    public void retrieveBusinessByIdTest() {
        Long id = testBusiness.getId();
        Business retrievedBusiness = businessService.retrieveBusinessById(id);

        assertNotNull("No matching business profile with given id could be retrieved from the business repository", retrievedBusiness);

        assertEquals("First name doesn't match", "Ada", retrievedBusiness.getFName());
        assertEquals("Last name doesn't match", "Polkanova", retrievedBusiness.getLName());
        assertEquals("Email doesn't match", "dogsledging@doggy-berlin.de", retrievedBusiness.getEMail());
        assertEquals("Phone doesn't match", "017668558497", retrievedBusiness.getPhone());
        assertEquals("Description doesn't match", "Hi, I'm Ada, the dog", retrievedBusiness.getDescription());
        assertEquals("Location doesn't match", "Berlin", retrievedBusiness.getServiceLocation());
        assertEquals("Username doesn't match", "polkaner", retrievedBusiness.getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveBusinessByIdBusinessNotFoundTest() {
        Long id = 12L;
        businessService.retrieveBusinessById(id);
    }

    @Test
    public void savePartiallyUpdatedBusinessProfileTest() {
        Long id = testBusiness.getId();
        BusinessUpdatePhotoDto businessUpdatePhotoDto = mockBusinessPhotoUpdate();
        Business updBusiness = businessService.savePartiallyUpdatedBusinessProfile(id, businessUpdatePhotoDto);

        assertNotNull("Updated business couldn't be saved", updBusiness);
        assertEquals("Updated photo doesn't match", "97d90e9f4692e6a.png", updBusiness.getPhoto());

    }


    @Test
    public void saveUpdatedBusinessProfileTest() {
        Long id = testBusiness.getId();
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();
        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);

        assertNotNull("Updated business couldn't be saved", updBusiness);

        assertEquals("Updated first name doesn't match", "Adelaida", updBusiness.getFName());
        assertEquals("Updated last name doesn't match", "Polkanova", updBusiness.getLName());
        assertEquals("Updated email doesn't match", "polkan@dogsrgods.com", updBusiness.getEMail());
        assertEquals("Updated phone doesn't match", "017628834523", updBusiness.getPhone());
        assertEquals("Updated description doesn't match", "Hi, I'm Ada, the hairiest guide in Berlin", updBusiness.getDescription());
        assertEquals("Updated service location doesn't match", "Charlottenburg", updBusiness.getServiceLocation());
        assertEquals("Updated username doesn't match", "polkan", updBusiness.getUsername());
        assertEquals("Updated photo doesn't match", "97d90e9f4692e6a.png", updBusiness.getPhoto());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void saveUpdatedBusinessProfileNotFoundTest() {
        Long id = 12L;
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();
        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    @Test(expected = EntityNotUniqueException.class)
    public void saveUpdatedBusinessProfileEmailAlreadyExistsTest() {
        Long id = testBusiness.getId();
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();

        Business updBusiness1 = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
        Business updBusiness2 = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    private BusinessPostDto mockBusinessNew() {
        BusinessPostDto businessNew = new BusinessPostDto();
        businessNew.setFName("Ada");
        businessNew.setLName("Polkanova");
        businessNew.setEMail("dogsledging@doggy.de");
        businessNew.setPhone("017668558497");
        businessNew.setDescription("Hi, I'm Ada, the dog");
        businessNew.setServiceLocation("berlin");
        businessNew.setUsername("polkaner");
        businessNew.setPassword("bones");
        businessNew.setPhoto(null);
        return businessNew;
    }

    private BusinessUpdateDto mockBusinessUpdate() {
        BusinessUpdateDto businessUpdate = new BusinessUpdateDto();
        businessUpdate.setFName("Adelaida");
        businessUpdate.setLName(null);
        businessUpdate.setEMail("polkan@dogsrgods.com");
        businessUpdate.setPhone("017628834523");
        businessUpdate.setDescription("Hi, I'm Ada, the hairiest guide in Berlin");
        businessUpdate.setServiceLocation("Charlottenburg");
        businessUpdate.setUsername("polkan");
        businessUpdate.setPhoto("97d90e9f4692e6a.png");

        return businessUpdate;
    }

    private BusinessUpdatePhotoDto mockBusinessPhotoUpdate() {
        BusinessUpdatePhotoDto businessPhotoUpdate = new BusinessUpdatePhotoDto();
        businessPhotoUpdate.setPhoto("97d90e9f4692e6a.png");

        return businessPhotoUpdate;
    }

    private Business mockBusiness() {
        Business business = new Business();
        business.setFName("Ada");
        business.setLName("Polkanova");
        business.setEMail("dogsledging@doggy-berlin.de");
        business.setPhone("017668558497");
        business.setDescription("Hi, I'm Ada, the dog");
        business.setServiceLocation("Berlin");
        business.setUsername("polkaner");
        business.setPassword("bones");
        business.setPhoto(null);
        return business;
    }
}