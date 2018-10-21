package de.berlin.lostberlin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.lostberlin.model.Business;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BusinessControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    public void test_get_businesses_success() throws Exception {

        mvc.perform(get("/businesses?location=berlin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fName").exists())
                .andExpect(jsonPath("$[0].lName").exists())
                .andExpect(jsonPath("$[0].serviceLocation").exists())
                .andDo(print());
    }

    @Test
    public void test_get_businesses_bad_request() throws Exception {
        mvc.perform(get("/businesses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void test_create_business_success() throws Exception {
        Business business = mockBusiness();
        String json = mapper.writeValueAsString(business);

        mvc.perform(post("/businesses")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void test_create_business_bad_request() throws Exception {
        Business businessShort = mockBusinessMissingFields();
        String json = mapper.writeValueAsString(businessShort);

        mvc.perform(post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_get_business_by_id_success() throws Exception {
        mvc.perform(get("/businesses/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fName").exists())
                .andExpect(jsonPath("$.lName").exists())
                .andExpect(jsonPath("$.serviceLocation").exists());
    }

    @Test
    public void test_get_business_by_id_not_found() throws Exception {
        mvc.perform(get("/businesses/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_update_business_success() throws Exception {
        Business business = mockBusiness();
        String json = mapper.writeValueAsString(business);
        mvc.perform(put("/businesses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void test_update_business_not_found() throws Exception {
        Business business = mockBusiness();
        String json = mapper.writeValueAsString(business);
        mvc.perform(put("/businesses/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    private Business mockBusiness() {
        Business business = new Business();
        business.setfName("Ada");
        business.setlName("Polkanova");
        business.setEmail("dogs4fun@dogmail.com");
        business.setServiceLocation("berlin");
        return business;
    }

    private Business mockBusinessMissingFields() {
        Business business = new Business();
        business.setfName("Ada");
        business.setServiceLocation("berlin");
        return business;
    }
}
