package de.berlin.lostberlin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.OrderRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
In order to run MVC tests I had to comment out the annotation @EnableJpaAuditing in the main class.
Unfortunately I didn't manage to fix it in a more appropriate way. Advises and hints will be highly appreciated!
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@WebMvcTest(OrderController.class)
public class OrderControllerMVCTest {

    @Autowired
    private MockMvc mvc;

//    @Autowired
//    private ObjectMapper mapper;

    @MockBean
    OrderRepository orderRepo;

    @Test
    public void test_get_all_orders_success() throws Exception {
        Order order = mockOrder();
        List<Order> allOrders = singletonList(order);
        given(this.orderRepo.findAll()).willReturn(allOrders);

        mvc.perform(get("/orders/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(order.getName()))
                .andExpect(jsonPath("$[0].email").value(order.getEmail()))
                .andDo(print());
        verify(orderRepo, times(1)).findAll();
        verifyNoMoreInteractions(orderRepo);
    }
//
//    @Test
//    public void test_get_orders_success() throws Exception {
//        Order order = mockOrderUpdate();
//        List<Order> orderListById = singletonList(order);
//        Long[] chosenBusinessIds ={4L,5L,6L};
//        given(this.orderController.getOrders(2L)).willReturn(orderListById);
//        mvc.perform(get("/orders/")
//                .contentType(MediaType.APPLICATION_JSON)
//                .param("business_id","2"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
//    }
//
//    @Test
//    public void test_create_order_success() throws Exception {
//        Order order = mockOrder();
//        String json = mapper.writeValueAsString(order);
//        given(this.orderController.createOrder(order)).willReturn(json);
//
//        mvc.perform(post("/orders")
//                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
//                .content(json))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void test_create_order_bad_request() throws Exception {
//        Order orderShort = mockOrderMissingFields();
//        String json = mapper.writeValueAsString(orderShort);
//        given(this.orderController.createOrder(orderShort)).willReturn(null);
//
//        mvc.perform(post("/orders")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(json))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Ignore // Probably it's just a very wrong test, but I'll leave it anyways. Is there any better way to test this?
//    @Test
//        public void test_create_order_failed_to_save() throws Exception {
//            Order order = mockOrder();
//            String json = mapper.writeValueAsString(order);
//            Order result = orderRepo.save(order);
//            given(this.orderRepo.save(order)).willReturn(null);
//
//
//            mvc.perform(post("/orders")
//                    .contentType(MediaType.APPLICATION_JSON_UTF8)
//                    .content(json))
//                    .andExpect(content().string("Failed to save order "+order.getOrderNr()));
//        }
//
//    @Test
//    public void test_get_by_order_nr_success() throws Exception {
//        Order order = mockOrderWithNr();
//        String json = mapper.writeValueAsString(order);
//        when(this.orderController.getByOrderNr("2018-10_00052")).thenReturn(order);
//
//        mvc.perform(get("/orders/{order_number}", "2018-10_00052")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//                .andExpect(jsonPath("$.name").value(order.getName()))
//                .andExpect(jsonPath("$.email").value(order.getEmail()));
//    }
//
//    @Test
//    public void test_get_by_order_nr_not_found() throws Exception {
//        Order order = mockOrderWithNr();
//        String json = mapper.writeValueAsString(order);
//        when(this.orderController.getByOrderNr("2018-10_00000")).thenThrow(new ResourceNotFoundException("Order", "order_number", "2018-10_00000"));
//
//        mvc.perform(get("/orders/{order_number}", "2018-10_00000")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//
//    @Test
//    public void test_update_order_status_success() throws Exception {
//        Order order = mockOrderUpdate();
//        String json = mapper.writeValueAsString(order);
//        when(this.orderController.updateOrderStatus(order.getOrderNr(), order)).thenReturn(order);
//        mvc.perform(put("/orders/{order_number}/status", order.getOrderNr())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isOk());
//    }
//
//    @Ignore //SAme problem here as in the BusinessControllerMVCTest (test_update_business_not_found())
//    @Test
//    public void test_update_order_status_not_found() throws Exception {
//        Order order = mockOrderUpdate();
//        String json = mapper.writeValueAsString(order);
//        when(this.orderController.updateOrderStatus("2018-10_00000", order)).thenThrow(new ResourceNotFoundException("Order", "order_number", "2018-10_00000"));
//        mvc.perform(put("/orders/{order_number}/status", "2018-10_00000")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void test_update_order_status_bad_request() throws Exception {
//        Order order = mockOrderMissingFields();
//        String json = mapper.writeValueAsString(order);
//
//        mvc.perform(put("/orders/{order_number}/status", "2018-10_00000")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isBadRequest());
//    }

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

    private Order mockOrderWithNr() {
        Order order = new Order();
        Long[] chosenBusinessIds = {4L, 5L, 6L};
        order.setOrderNr("2018-10_00052");
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
        order.setOrderNr("2018-10_00102");
        order.setName("Alena");
        order.setEmail("aoneko@gmx.de");
        order.setBusinessId(2L);
        order.setStatus("confirmed");
        return order;
    }
}
