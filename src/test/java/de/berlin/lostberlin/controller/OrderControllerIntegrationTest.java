package de.berlin.lostberlin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.lostberlin.model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void test_get_all_orders_success() throws Exception {
        mvc.perform(get("/orders/all")
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].name").exists())
                .andExpect(jsonPath("$[1].email").exists())
                .andDo(print());
    }

    @Test
    public void test_get_orders_success() throws Exception {
        mvc.perform(get("/orders/")
        .contentType(MediaType.APPLICATION_JSON)
        .param("business_id","1"))
                .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void test_create_order_success() throws Exception {
        Order order = mockOrder();
        String json = mapper.writeValueAsString(order);

        mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));
    }

    @Test
    public void test_create_order_bad_request() throws Exception {
        Order order = mockOrderMissingFields();
        String json = mapper.writeValueAsString(order);

        mvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_get_by_order_nr_success() throws Exception {
        mvc.perform(get("/orders/{order_number}", "2018-10_00052"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    public void test_get_by_order_nr_not_found() throws Exception {
        mvc.perform(get("/orders/{order_number}", "2018-10_00000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void test_update_order_status_success() throws Exception {
        Order order = mockOrderUpdate();
        String json = mapper.writeValueAsString(order);

        mvc.perform(put("/orders/{order_number}/status", "2018-10_00102")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void test_update_order_status_not_found() throws Exception {
        Order order = mockOrderUpdate();
        String json = mapper.writeValueAsString(order);

        mvc.perform(put("/orders/{order_number}/status", "2018-10_00000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_update_order_status_bad_request() throws Exception {
        Order order = mockOrderMissingFields();
        String json = mapper.writeValueAsString(order);

        mvc.perform(put("/orders/{order_number}/status", "2018-10_00000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    private Order mockOrder() {
        Order order = new Order();
        Long[] chosenBusinessIds ={4L,5L,6L};
        order.setName("Maxim");
        order.setChosenBusinessIds(chosenBusinessIds);
        order.setEmail("animamedianatura@gmx.com");
        order.setPhone("017628834523");
        order.setDescription("Hi, I'm Max, the tourist!");
        return order;
    }

    private Order mockOrderMissingFields() {
        Order order = new Order();
        order.setName("Maxim");
        order.setPhone("017628834523");
        return order;
    }

    private Order mockOrderUpdate(){
        Order order = new Order();
        order.setName("Alena");
        order.setEmail("aoneko@gmx.de");
        order.setBusinessId(2L);
        order.setStatus("confirmed");
        return order;
    }
}