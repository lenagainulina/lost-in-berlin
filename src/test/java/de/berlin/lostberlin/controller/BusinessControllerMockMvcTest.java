package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.business.client.BusinessPostDto;
import de.berlin.lostberlin.model.business.client.BusinessShortDao;
import de.berlin.lostberlin.model.business.client.BusinessUpdateDto;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.service.BusinessServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    @RunWith(SpringRunner.class)
    @WebAppConfiguration
    @WebMvcTest(BusinessController.class)
    public class BusinessControllerMockMvcTest {
        @Autowired
        private MockMvc mvc;

        @Autowired
        private ObjectMapper mapper;

        @MockBean
        private BusinessServiceImpl businessService;

        @Test
        public void test_get_businesses_success() throws Exception {
            String location = "berlin";
            BusinessShortDao businessShort = mockBusinessShort();
            List<BusinessShortDao> allBusinesses = singletonList(businessShort);
            given(this.businessService.retrieveBusinessByLocation(location)).willReturn(allBusinesses);

            mvc.perform(get("/businesses?location=berlin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].fname").value(businessShort.getFName()))
                    .andExpect(jsonPath("$[0].lname").value(businessShort.getLName()))
                    .andExpect(jsonPath("$[0].description").value(businessShort.getDescription()))
                    .andExpect(jsonPath("$[0].serviceLocation").value(businessShort.getServiceLocation()))
                    .andDo(print());
            verify(businessService, times(1)).retrieveBusinessByLocation(location);
            verifyNoMoreInteractions(businessService);
        }
        @Test
        public void test_get_businesses_bad_request() throws Exception {
            String location = "";
            BusinessShortDao businessShort = mockBusinessShort();
            List<BusinessShortDao> allBusinesses = singletonList(businessShort);
            given(this.businessService.retrieveBusinessByLocation(location)).willThrow(new ResourceNotFoundException());
            mvc.perform(get("/businesses")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
        @Test
        public void test_create_business_success() throws Exception {
            BusinessPostDto businessNew = mockBusinessNew();
            Business business = mockBusiness();
            String json = mapper.writeValueAsString(businessNew);
           given(this.businessService.saveNewlyCreatedBusinessProfile(businessNew)).willReturn(business);
            mvc.perform(post("/businesses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }
        @Test
        public void test_create_business_bad_request() throws Exception {
            BusinessPostDto businessIncomplete = mockBusinessMissingFields();
            String json = mapper.writeValueAsString(businessIncomplete);
            when(this.businessService.saveNewlyCreatedBusinessProfile(businessIncomplete)).thenReturn(null);
            mvc.perform(post("/businesses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }
        @Test
        public void test_get_business_by_id_success() throws Exception {
            Business business = mockBusiness();
            when(this.businessService.retrieveBusinessById(1L)).thenReturn(business);
            mvc.perform(get("/businesses/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.fname").value(business.getFName()))
                    .andExpect(jsonPath("$.lname").value(business.getLName()))
                    .andExpect(jsonPath("$.email").value(business.getEMail()))
                    .andExpect(jsonPath("$.phone").value(business.getPhone()))
                    .andExpect(jsonPath("$.description").value(business.getDescription()))
                    .andExpect(jsonPath("$.serviceLocation").value(business.getServiceLocation()))
                    .andExpect(jsonPath("$.username").value(business.getUsername()));
            verify(businessService, times(1)).retrieveBusinessById(1L);
            verifyNoMoreInteractions(businessService);
        }
        @Test
        public void test_get_business_by_id_not_found() throws Exception {
            when(this.businessService.retrieveBusinessById(2L)).thenThrow(new ResourceNotFoundException("Not found", "business profile", 2L));
            mvc.perform(get("/businesses/{id}", 2)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
        @Test
        public void test_update_business_success() throws Exception {
            BusinessUpdateDto businessUpdate = mockBusinessUpdate();
            Business business = mockBusiness();
            Business updatedBusiness = mockUpdatedBusiness();
            String json = mapper.writeValueAsString(businessUpdate);
            when(this.businessService.saveUpdatedBusinessProfile(business.getId(), businessUpdate)).thenReturn(updatedBusiness);
            mvc.perform(put("/businesses/{id}", business.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fname").value(updatedBusiness.getFName()))
                    .andExpect(jsonPath("$.lname").value(updatedBusiness.getLName()))
                    .andExpect(jsonPath("$.email").value(updatedBusiness.getEMail()))
                    .andExpect(jsonPath("$.phone").value(updatedBusiness.getPhone()))
                    .andExpect(jsonPath("$.description").value(updatedBusiness.getDescription()))
                    .andExpect(jsonPath("$.serviceLocation").value(updatedBusiness.getServiceLocation()))
                    .andExpect(jsonPath("$.username").value(updatedBusiness.getUsername()));
        }

        @Test
        public void test_update_business_not_found() throws Exception {
            BusinessUpdateDto businessUpdate = mockBusinessUpdate();
            String json = mapper.writeValueAsString(businessUpdate);
            when(this.businessService.saveUpdatedBusinessProfile(2L, businessUpdate)).thenThrow(new ResourceNotFoundException("Not found", "business profile", 2L));
            mvc.perform(put("/businesses/{id}", 2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
        private BusinessShortDao mockBusinessShort() {
            BusinessShortDao businessShort = new BusinessShortDao();
            businessShort.setFName(mockBusiness().getFName());
            businessShort.setLName(mockBusiness().getLName());
            businessShort.setDescription(mockBusiness().getDescription());
            businessShort.setServiceLocation(mockBusiness().getServiceLocation());
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
            businessNew.setPassword("bone$");
            return businessNew;
        }

        private BusinessUpdateDto mockBusinessUpdate() {
            BusinessUpdateDto businessUpdate = new BusinessUpdateDto();
            businessUpdate.setFName("Adelaida");
            businessUpdate.setLName("Polkanova");
            businessUpdate.setEMail("polkan@dogsrgods.com");
            return businessUpdate;
        }

        private Business mockUpdatedBusiness() {
            Business business = new Business();
            business.setId(mockBusiness().getId());
            business.setFName(mockBusinessUpdate().getFName());
            business.setLName(mockBusinessUpdate().getLName());
            business.setEMail(mockBusinessUpdate().getEMail());
            business.setPhone(mockBusiness().getPhone());
            business.setDescription(mockBusiness().getDescription());
            business.setServiceLocation(mockBusiness().getServiceLocation());
            business.setUsername(mockBusiness().getUsername());
            business.setPassword(mockBusiness().getPassword());
            return business;
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
            return business;
        }
        private BusinessPostDto mockBusinessMissingFields() {
            BusinessPostDto businessIncomplete = new BusinessPostDto();
            businessIncomplete.setFName("Ada");
            businessIncomplete.setServiceLocation("berlin");
            return businessIncomplete;
        }
    }

