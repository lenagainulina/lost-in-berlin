package de.berlin.lostberlin.service;

import de.berlin.lostberlin.ApplicationConfig;
import de.berlin.lostberlin.TestConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class BusinessServiceTest {

    @Autowired
    BusinessService businessService;

    @TestConfiguration
    @EnableJpaAuditing
    public static class BusinessServiceTestConfiguration{
        @Autowired
        private BusinessRepository businessRepository;

        @Bean
        public BusinessService createBusinessService() {
            return new BusinessServiceImpl(businessRepository);
        }
    }

    @Before
    public void setUp() {
        //businessService =
    }

    @Test
    public void retrieveBusinessByLocationTest() {

        String location = "Berlin";
        BusinessShortDao businessShortDao = mockBusinessShort();

        List<BusinessShortDao> allBusinesses = singletonList(businessShortDao);

        List<BusinessShortDao> business = businessService.retrieveBusinessByLocation(location);

        assertFalse("List of businesses is empty", business.isEmpty());
        // checks that the structure has been interpreted correctly
        assertEquals("First name of the first object in the list is missing", "Ada", business.get(0).getFName());
        assertEquals("Last name of the first object in the list is missing", "Polkanova", business.get(0).getLName());
        assertEquals("Description of the first object in the list is missing", "Hi, I'm Ada, the dog", business.get(0).getDescription());
        assertEquals("Location of the first object in the list is missing", "berlin", business.get(0).getServiceLocation());
        assertEquals("Photo of the first object in the list is missing", null, business.get(0).getPhoto());
    }

    /*
    This test keeps failing, because of the NullPointer exception. I assume that I need to mock the bean validation of
    the BusinessPostDto (@NotBlank, @Email). I couldn't figure out how to do that. As fas as I understand I should mock
    Validation.buildDefaultValidatorFactory() and then make and then make an assumption in test that it returns empty
    set of ConstraintViolations. I couldn't figure out how to do it correct, keep getting an NPE when I try the following:
    given(factory.getValidator().validate(businessNew)).willReturn(is(empty()));
    But perhaps I'm completely wrong and the test is failing for another reason?
     */
    @Test
    public void saveNewlyCreatedBusinessProfileTest() {
        BusinessPostDto businessNew = mockBusinessNew();
        Business originalBusinessProfile = mockBusiness();

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
        business.setCreatedAt(new Date());

        return business;
    }

}