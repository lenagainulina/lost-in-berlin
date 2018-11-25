package de.berlin.lostberlin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.ChosenBusiness;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;
import de.berlin.lostberlin.service.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@WebMvcTest(OrderController.class)
public class OrderControllerMockMvcTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderServiceImpl orderService;

    @Test
    public void test_retrieve_all_orders_success() throws Exception {
        orderService.retrieveAllOrders();
        Order order = mockOrder();
        List<Order> allOrders = singletonList(order);
        given(this.orderService.retrieveAllOrders()).willReturn(allOrders);

        mvc.perform(get("/orders/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNr").value(order.getOrderNr()))
                .andExpect(jsonPath("$[0].businessId").value(order.getBusinessId()))
                //  .andExpect(jsonPath("$[0].status").value(order.getStatus()))
                .andExpect(jsonPath("$[0].name").value(order.getName()))
                .andExpect(jsonPath("$[0].phone").value(order.getPhone()))
                //  .andExpect(jsonPath("$[0].date").value(order.getDate()))
                .andExpect(jsonPath("$[0].time").value(order.getTime()))
                .andExpect(jsonPath("$[0].participantsNr").value(order.getParticipantsNr()))
                .andExpect(jsonPath("$[0].description").value(order.getDescription()))
                .andExpect(jsonPath("$[0].email").value(order.getEMail()))
                .andDo(print());
        verify(orderService, times(2)).retrieveAllOrders();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void test_retrieve_all_orders_not_found() throws Exception {
        Order order = mockOrder();
        List<Order> allOrders = singletonList(order);
        given(this.orderService.retrieveAllOrders()).willThrow(new ResourceNotFoundException());

        mvc.perform(get("/orders/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_get_order_by_business_id_success() throws Exception {
        Long id = 1L;
        orderService.retrieveOrdersByBusinessId(id);
        OrderShortDao order = mockShortOrder();
        List<OrderShortDao> allOrders = singletonList(order);
        given(this.orderService.retrieveOrdersByBusinessId(id)).willReturn(allOrders);

        mvc.perform(get("/orders/?business_id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].name").value(order.getName()))
                //    .andExpect(jsonPath("$[0].date").value(order.getDate()))
                .andExpect(jsonPath("$[0].time").value(order.getTime()))
                .andExpect(jsonPath("$[0].participantsNr").value(order.getParticipantsNr()))
                .andExpect(jsonPath("$[0].description").value(order.getDescription()))
                .andDo(print());
        verify(orderService, times(2)).retrieveOrdersByBusinessId(id);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void test_get_order_by_business_id_not_found() throws Exception {
        when(this.orderService.retrieveOrdersByBusinessId(12L)).thenThrow(new ResourceNotFoundException("Not found", "order", 12L));
        mvc.perform(get("/orders/?business_id=12")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_get_order_by_order_nr_success() throws Exception {
        String orderNr = "2018-11_00452";
        orderService.retrieveOrderByOrderNr(orderNr);
        OrderStatusDao order = mockStatusForm();
        given(this.orderService.retrieveOrderByOrderNr(orderNr)).willReturn(order);

        mvc.perform(get("/orders/{order_number}", "2018-11_00452"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value(order.getName()))
                //    .andExpect(jsonPath("$.date").value(order.getDate()))
                .andExpect(jsonPath("$.time").value(order.getTime()))
                .andExpect(jsonPath("$.businessId").value(order.getBusinessId()));
        //   .andExpect(jsonPath("$.status").value(order.getStatus()));
        verify(orderService, times(2)).retrieveOrderByOrderNr(orderNr);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void test_get_order_by_order_nr_not_found() throws Exception {
        when(this.orderService.retrieveOrderByOrderNr("2018-11_00001")).thenThrow(new ResourceNotFoundException("Order not found"));
        mvc.perform(get("/orders/{order_number}", "2018-11_00001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void test_create_order_success() throws Exception {
        OrderPostDto orderNew = mockOrderNew();
        Order order = mockOrder();
        String json = mapper.writeValueAsString(orderNew);
        given(this.orderService.saveOrderProfile(orderNew)).willReturn(order);
        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void test_create_order_bad_request() throws Exception {
        OrderPostDto orderIncomplete = mockOrderIncomplete();
        String json = mapper.writeValueAsString(orderIncomplete);
        when(this.orderService.saveOrderProfile(orderIncomplete)).thenReturn(null);
        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_confirm_order_success() throws Exception {
        OrderFullDao fullOrder = mockfullOrder();
        given(this.orderService.confirmOrder("2018-11_00502", "CONFIRMED", 1L)).willReturn(fullOrder);
        mvc.perform(put("/orders/{order_number}/confirmation/?status=CONFIRMED&business_id=1", "2018-11_00502"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value(fullOrder.getName()))
                .andExpect(jsonPath("$.phone").value(fullOrder.getPhone()))
                .andExpect(jsonPath("$.email").value(fullOrder.getEMail()))
                //  .andExpect(jsonPath("$.date").value(fullOrder.getDate()))
                .andExpect(jsonPath("$.time").value(fullOrder.getTime()))
                .andExpect(jsonPath("$.participantsNr").value(fullOrder.getParticipantsNr()))
                .andExpect(jsonPath("$.description").value(fullOrder.getDescription()))
                .andExpect(jsonPath("$.businessId").value(fullOrder.getBusinessId()))
                //  .andExpect(jsonPath("$.status").value(fullOrder.getStatus()))
                .andExpect(jsonPath("$.orderNr").value(fullOrder.getOrderNr()));
    }

    @Test
    public void test_update_order_status_to_closed_success() throws Exception {
        orderService.updateOrderStatus("2018-11_00502", "CLOSED", 1L);
        mvc.perform(put("/orders/{order_number}/status?status=CLOSED&business_id=1", "2018-11_00502"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_update_order_status_to_pending_success() throws Exception {
        orderService.updateOrderStatus("2018-11_00502", "PENDING", 1L);
        mvc.perform(put("/orders/{order_number}/status?status=PENDING&business_id=1", "2018-11_00502"))
                .andExpect(status().isOk());
    }

    private OrderPostDto mockOrderIncomplete() {
        OrderPostDto orderNew = new OrderPostDto();
        Long[] chosenBusinessIds = {};
        orderNew.setChosenBusinessIds(chosenBusinessIds);
        orderNew.setName("Lena");
        orderNew.setDate(LocalDate.of(2018, 12, 12));
        orderNew.setTime("around 1 p.m.");
        orderNew.setParticipantsNr(2L);
        orderNew.setDescription("Hi, I'm Lena, will you be my guide?");
        return orderNew;
    }

    private OrderPostDto mockOrderNew() {
        OrderPostDto orderNew = new OrderPostDto();
        Long[] chosenBusinessIds = {1L, 2L, 3L};
        orderNew.setChosenBusinessIds(chosenBusinessIds);
        orderNew.setName("Lena");
        orderNew.setPhone("017668558497");
        orderNew.setEMail("aoneko@gmx.de");
        orderNew.setDate(LocalDate.of(2018, 12, 12));
        orderNew.setTime("around 1 p.m.");
        orderNew.setParticipantsNr(2L);
        orderNew.setDescription("Hi, I'm Lena, will you be my guide?");
        return orderNew;
    }

    private Order mockOrder() {
        Order order = new Order();
        StatusTypes PENDING = StatusTypes.PENDING;

        order.setOrderNr("2018-11_00502");
        order.setBusinessId(1L);
        order.setStatus(PENDING);
        order.setName(mockOrderNew().getName());
        order.setPhone(mockOrderNew().getPhone());
        order.setEMail(mockOrderNew().getEMail());
        order.setDate(mockOrderNew().getDate());
        order.setTime(mockOrderNew().getTime());
        order.setParticipantsNr(mockOrderNew().getParticipantsNr());
        order.setDescription(mockOrderNew().getDescription());
        return order;
    }

    private OrderFullDao mockfullOrder() {
        OrderFullDao order = new OrderFullDao();
        order.setName(mockOrder().getName());
        order.setPhone(mockOrder().getPhone());
        order.setEMail(mockOrder().getEMail());
        order.setDate(mockOrder().getDate());
        order.setTime(mockOrder().getTime());
        order.setParticipantsNr(mockOrder().getParticipantsNr());
        order.setDescription(mockOrder().getDescription());
        order.setBusinessId(mockOrder().getBusinessId());
        order.setStatus(mockOrder().getStatus());
        order.setOrderNr(mockOrder().getOrderNr());
        return order;
    }

    private OrderShortDao mockShortOrder() {
        OrderShortDao order = new OrderShortDao();
        order.setName(mockOrder().getName());
        order.setDate(mockOrder().getDate());
        order.setTime(mockOrder().getTime());
        order.setParticipantsNr(mockOrder().getParticipantsNr());
        order.setDescription(mockOrder().getDescription());
        return order;
    }

    private OrderStatusDao mockStatusForm() {
        OrderStatusDao order = new OrderStatusDao();
        order.setName(mockOrder().getName());
        order.setDate(mockOrder().getDate());
        order.setTime(mockOrder().getTime());
        order.setBusinessId(mockOrder().getBusinessId());
        order.setStatus(mockOrder().getStatus());
        return order;
    }

    private ChosenBusiness mockChosenBusiness() {
        ChosenBusiness chosenBusiness = new ChosenBusiness();
        chosenBusiness.setId(1L);
        chosenBusiness.setBusinessId(1L);
        chosenBusiness.setOrderNr(mockOrder().getOrderNr());
        return chosenBusiness;
    }
}


