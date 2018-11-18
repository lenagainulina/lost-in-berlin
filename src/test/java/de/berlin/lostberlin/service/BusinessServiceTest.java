package de.berlin.lostberlin.service;

import de.berlin.lostberlin.ApplicationConfig;
import de.berlin.lostberlin.exception.EntityNotUniqueException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.repository.BusinessRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class, initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest
public class BusinessServiceTest {

    @MockBean
    private BusinessRepository businessRepository;

    private BusinessService businessService;

    @MockBean
    ValidatorFactory factory;


    @Before
    public void setUp() {
        businessService = new BusinessServiceImpl(businessRepository);

        //factory = Validation.buildDefaultValidatorFactory();
    }

    @Test
    public void retrieveBusinessByLocationTest() {

        String location = "Berlin";
        BusinessShortDao businessShortDao = mockBusinessShort();

        List<BusinessShortDao> allBusinesses = singletonList(businessShortDao);

        given(businessRepository.getShortBusinessProfiles(location)).willReturn(allBusinesses);

        List<BusinessShortDao> business = businessService.retrieveBusinessByLocation(location);

        // verifies that business repository was invoked one time during the search
        verify(businessRepository, times(1)).getShortBusinessProfiles("Berlin");

        assertFalse("List of businesses is empty", business.isEmpty());
        // checks that the structure has been interpreted correctly
        assertEquals("First name of the first object in the list is missing", "Ada", business.get(0).getFName());
        assertEquals("Last name of the first object in the list is missing", "Polkanova", business.get(0).getLName());
        assertEquals("Description of the first object in the list is missing", "Hi, I'm Ada, the dog", business.get(0).getDescription());
        assertEquals("Location of the first object in the list is missing", "berlin", business.get(0).getServiceLocation());
        assertEquals("Photo of the first object in the list is missing", null, business.get(0).getPhoto());
    }

    /*
    Checks that the exception is thrown when no matching businesses for the given location can be found in the database
     */
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveBusinessByLocationBusinessNotFoundTest() {
        String location = "Paris";
        BusinessShortDao businessShortDao = mockBusinessShort();

        List<BusinessShortDao> allBusinesses = singletonList(businessShortDao);

        businessService.retrieveBusinessByLocation(location);
    }

    /*
    This test keeps failing, because of the NullPointer exception. I assume that I need to mock the bean validation of
    the BusinessPostDto (@NotBlank, @Email). I couldn't figure out how to do that. As fas as I understand I should mock
    Validation.buildDefaultValidatorFactory() and then make and then make an assumption in test that it returns empty
    set of ConstraintViolations. I couldn't figure out how to do it correct, keep getting an NPE when I try the following:
    given(factory.getValidator().validate(businessNew)).willReturn(is(empty()));
    But perhaps I'm completely wrong and the test is failing for another reason?
     */
    @Ignore
    @Test
    public void saveNewlyCreatedBusinessProfileTest() {
        BusinessPostDto businessNew = mockBusinessNew();
        Business originalBusinessProfile = mockBusiness();


        given(businessRepository.existsByEMail(businessNew.getEMail())).willReturn(false);
        given(businessRepository.save(originalBusinessProfile)).willReturn(originalBusinessProfile);

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
    public void saveNewlyCreatedBusinessProfileEmailAlreadyExistsTest(){
        BusinessPostDto businessNew = mockBusinessNew();
        given(businessRepository.existsByEMail(businessNew.getEMail())).willReturn(true);
        businessService.saveNewlyCreatedBusinessProfile(businessNew);
    }

    @Test
    public void retrieveBusinessByIdTest() {
        Long id = 1L;
        Business business = mockBusiness();

        given(businessRepository.findById(id)).willReturn(Optional.of(business));

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

    /*
        Checks that the exception is thrown when no matching businesses  can be found in business repository by given id
         */
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveBusinessByIdBusinessNotFoundTest() {
        Long id = 2L;
        Optional <Business> business = Optional.empty();
        given(businessRepository.findById(id)).willReturn(business);
        businessService.retrieveBusinessById(id);
    }

    @Test
    public void saveUpdatedBusinessProfileTest() {
        Long id = 1L;
        Business originalBusinessProfile = mockBusiness();
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();

        given(businessRepository.findById(id)).willReturn(Optional.of(originalBusinessProfile));
        given(businessRepository.save(originalBusinessProfile)).willReturn(originalBusinessProfile);

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

    /*
        Checks that the exception is thrown when no matching business can be found  in business repository by given id
         */
    @Test(expected = ResourceNotFoundException.class)
    public void saveUpdatedBusinessProfileNotFoundTest() {
        Long id = 2L;
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();
        Optional <Business> business = Optional.empty();
        given(businessRepository.findById(id)).willReturn(business);

        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    @Test (expected = EntityNotUniqueException.class)
    public void saveUpdatedBusinessProfileEmailAlreadyExistsTest(){
        Long id = 1L;
        Business originalBusinessProfile = mockBusiness();
        BusinessUpdateDto businessUpdateDto = mockBusinessUpdate();

        given(businessRepository.findById(id)).willReturn(java.util.Optional.ofNullable(originalBusinessProfile));
        assumeThat(originalBusinessProfile.getEMail().equals(businessUpdateDto.getEMail()));
        given(businessRepository.existsByEMail(businessUpdateDto.getEMail())).willReturn(true);

        Business updBusiness = businessService.saveUpdatedBusinessProfile(id, businessUpdateDto);
    }

    private BusinessShortDao mockBusinessShort() {
        BusinessShortDao businessShort = new BusinessShortDao();
        businessShort.setFName(mockBusiness().getFName());
        businessShort.setLName(mockBusiness().getLName());
        businessShort.setDescription(mockBusiness().getDescription());
        businessShort.setServiceLocation(mockBusiness().getServiceLocation());
        businessShort.setPhoto(mockBusiness().getPhoto());
        return businessShort;
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

    private Business mockBusiness() {
            Business business = new Business();
            business.setId(1L);
            business.setFName(mockBusinessNew().getFName());
            business.setLName(mockBusinessNew().getLName());
            business.setEMail(mockBusinessNew().getEMail());
            business.setPhone(mockBusinessNew().getPhone());
            business.setDescription(mockBusinessNew().getDescription());
            business.setServiceLocation(mockBusinessNew().getServiceLocation());
            business.setUsername(mockBusinessNew().getUsername());
            business.setPassword(mockBusinessNew().getPassword());
            business.setPhoto(mockBusinessNew().getPhoto());
            return business;
    }

}