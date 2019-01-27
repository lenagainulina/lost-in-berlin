package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.OperationForbiddenException;
import de.berlin.lostberlin.exception.ResourceConflictException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.exception.ResourceNotSavedException;
import de.berlin.lostberlin.model.business.persistence.Business;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.ChosenBusiness;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;
import de.berlin.lostberlin.repository.BusinessRepository;
import de.berlin.lostberlin.repository.ChosenBusinessRepository;
import de.berlin.lostberlin.repository.OrderRepository;
import de.berlin.lostberlin.service.mail.MailService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static de.berlin.lostberlin.model.order.persistence.StatusTypes.CONFIRMED;
import static de.berlin.lostberlin.model.order.persistence.StatusTypes.PENDING;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderServiceTest {

    @TestConfiguration
    @EnableJpaAuditing
    public static class OrderServiceTestConfiguration {
        @Autowired
        private OrderRepository orderRepo;
        @Autowired
        private BusinessRepository businessRepo;
        @Autowired
        private ChosenBusinessRepository chosenBusinessRepo;

        @MockBean
        private MailService mailService;

        @Bean
        public OrderService createOrderService() {
            return new OrderServiceImpl(orderRepo, businessRepo, chosenBusinessRepo, mailService);
        }
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestEntityManager entityManager;

    @MockBean
    private BusinessRepository businessRepository;

    private Order testOrder;


    @Before
    public void setUp() {
        testOrder = mockOrder();
        entityManager.persist(testOrder);
    }

    @Test
    public void retrieveAllOrdersTest() {
        List<Order> orderList = orderService.retrieveAllOrders();

        assertFalse("List of order is empty", orderList.isEmpty());

        assertEquals("Name of the first object in the list doesn't match", "Lena", orderList.get(0).getName());
        assertEquals("Phone of the first object in the list doesn't match", "017668558497", orderList.get(0).getPhone());
        assertEquals("Email of the first object in the list doesn't match", "aoneko@gmx.de", orderList.get(0).getEMail());
        assertEquals("Date of the first object in the list doesn't match", LocalDate.of(2018, 12, 12), orderList.get(0).getDate());
        assertEquals("Time of the first object in the list doesn't match", "around 1 p.m.", orderList.get(0).getTime());
        assertEquals("Participants number of the first object in the list doesn't match", 2L, orderList.get(0).getParticipantsNr());
        assertEquals("Description of the first object in the list doesn't match", "Hi, I'm Lena, will you be my guide?", orderList.get(0).getDescription());
    }

    @Ignore //run with commented @Before
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveAllOrdersNotFoundTest() {
        orderService.retrieveAllOrders();
    }

    @Test
    public void retrieveOrdersByBusinessIdTest() {
        Long[] chosenBusinessIds = {1L, 2L, 3L};
        orderService.saveChosenBusinesses(testOrder, chosenBusinessIds);
        Long id = testOrder.getBusinessId();
        List<OrderShortDao> orderList = orderService.retrieveOrdersByBusinessId(id);

        assertFalse("List of order is empty", orderList.isEmpty());

        assertEquals("Name of the first object in the list doesn't match", "Lena", orderList.get(0).getName());
        assertEquals("Date of the first object in the list doesn't match", LocalDate.of(2018, 12, 12), orderList.get(0).getDate());
        assertEquals("Time of the first object in the list doesn't match", "around 1 p.m.", orderList.get(0).getTime());
        assertEquals("Participants number of the first object in the list doesn't match", 2L, orderList.get(0).getParticipantsNr());
        assertEquals("Description of the first object in the list doesn't match", "Hi, I'm Lena, will you be my guide?", orderList.get(0).getDescription());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveOrdersByBusinessIdNotFoundTest() {
        Long id = 12L;
        orderService.retrieveOrdersByBusinessId(id);
    }

    @Test
    public void retrieveOrderByOrderNrTest() {
        String orderNr = testOrder.getOrderNr();
        Long id = testOrder.getBusinessId();
        OrderStatusDao orderStatus = orderService.retrieveOrderByOrderNr(orderNr);
        StatusTypes confirmed = CONFIRMED;

        assertEquals("Name doesn't match", "Lena", orderStatus.getName());
        assertEquals("Date doesn't match", LocalDate.of(2018, 12, 12), orderStatus.getDate());
        assertEquals("Time doesn't match", "around 1 p.m.", orderStatus.getTime());
        assertEquals("Business id doesn't match", id, orderStatus.getBusinessId());
        assertEquals("Status doesn't match", confirmed, orderStatus.getStatus());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveOrdersByOrderNrNotFoundTest() {
        String orderNr = "2018-11_00001";
        orderService.retrieveOrderByOrderNr(orderNr);
    }

    @Test
    public void saveOrderProfileTest() {
        OrderPostDto orderNew = mockOrderNew();
        Order savedOrder = orderService.saveOrderProfile(orderNew);

        assertNotNull("order profile could not be saved", savedOrder);

        assertEquals("Name doesn't match", "Lena", savedOrder.getName());
        assertEquals("Phone doesn't match", "017668558497", savedOrder.getPhone());
        assertEquals("Email doesn't match", "aoneko@gmx.de", savedOrder.getEMail());
        assertEquals("Date doesn't match", LocalDate.of(2018, 12, 12), savedOrder.getDate());
        assertEquals("Time doesn't match", "around 1 p.m.", savedOrder.getTime());
        assertEquals("Participants number doesn't match", 2L, savedOrder.getParticipantsNr());
        assertEquals("Description doesn't match", "Hi, I'm Lena, will you be my guide?", savedOrder.getDescription());
    }

    @Ignore //Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void saveOrderProfileNotSavedTest() {
        OrderPostDto orderNew = mockOrderNew();
        orderService.saveOrderProfile(orderNew);
    }


    @Test
    public void saveChosenBusinessesTest() {
        Long[] chosenBusinessIds = {1L, 2L, 3L};
        ChosenBusiness savedBusiness = orderService.saveChosenBusinesses(testOrder, chosenBusinessIds);

        assertFalse(savedBusiness.getOrderNr().isEmpty());
        assertNotNull(savedBusiness.getBusinessId());
    }

    @Ignore // Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void saveChosenBusinessesNotSavedTest() {
        Order testOrder = mockOrder();
        entityManager.persist(testOrder);
        Long[] chosenBusinessIds = {1L, 2L, 3L};
        orderService.saveChosenBusinesses(testOrder, chosenBusinessIds);
    }

    @Test
    public void updateOrderStatusToPendingTest() {
        String orderNr = testOrder.getOrderNr();
        String status = "PENDING";
        Long businessId = null;
        StatusTypes pending = PENDING;
        orderService.updateOrderStatus(orderNr, status, businessId);
        OrderStatusDao order = orderService.retrieveOrderByOrderNr(orderNr);

        assertEquals("Status doesn't match", pending, order.getStatus());
        assertEquals("Business id doesn't match", null, order.getBusinessId());
    }

    @Test
    public void updateOrderStatusToClosedTest() {
        String orderNr = testOrder.getOrderNr();
        String status = "CLOSED";
        StatusTypes closed = StatusTypes.CLOSED;
        Long businessId =testOrder.getBusinessId();
        Business business = mockBusiness();

        given(businessRepository.findById(businessId)).willReturn(Optional.ofNullable(business));

        orderService.updateOrderStatus(orderNr, status, businessId);
        OrderStatusDao order = orderService.retrieveOrderByOrderNr(orderNr);

        assertEquals("Status doesn't match", closed, order.getStatus());
        assertEquals("Business id doesn't match", businessId, order.getBusinessId());
    }

    @Test(expected = OperationForbiddenException.class)
    public void updateOrderStatusToClosedForbiddenTest() {
        Order testOrder = mockOrder();
        entityManager.persist(testOrder);
        String orderNr = testOrder.getOrderNr();
        Long businessId = testOrder.getBusinessId();
        StatusTypes closed = StatusTypes.CLOSED;
        testOrder.setStatus(closed);
        String status = "CLOSED";
        Business business = mockBusiness();

        given(businessRepository.findById(1L)).willReturn(Optional.ofNullable(business));

        orderService.updateOrderStatus(orderNr, status, businessId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateOrderStatusBusinessNotFoundTest() {
        String orderNr = "2018-11_00352";
        String status = "CLOSED";
        Long businessId = 12L;

        orderService.updateOrderStatus(orderNr, status, businessId);
    }

    @Test
    public void confirmOrderTest() {
        testOrder.setBusinessId(null);
        StatusTypes pending = StatusTypes.PENDING;
        testOrder.setStatus(pending);
        String orderNr = testOrder.getOrderNr();
        String status = "CONFIRMED";
        StatusTypes confirmed = StatusTypes.CONFIRMED;
        Long businessId = 1L;

        Business business = mockBusiness();
        given(businessRepository.findById(1L)).willReturn(Optional.ofNullable(business));

        OrderFullDao confirmedOrder = orderService.confirmOrder(orderNr, status, businessId);

        assertEquals("Status doesn't match", confirmed, confirmedOrder.getStatus());
        assertEquals("Business id doesn't match", businessId, confirmedOrder.getBusinessId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void confirmOrderNotFoundTest() {
        String orderNr = "2018-11_00001";
        String status = "CONFIRMED";
        Long businessId = testOrder.getBusinessId();

        orderService.confirmOrder(orderNr, status, businessId);
    }

    @Ignore // Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void confirmOrderNotSavedTest() {
        String orderNr = "2018-11_00502";
        String status = "CONFIRMED";
        Long businessId = testOrder.getBusinessId();

        orderService.confirmOrder(orderNr, status, businessId);
    }

    @Test(expected = ResourceConflictException.class)
    public void confirmOrderConflictTest() {
        String orderNr = testOrder.getOrderNr();
        String status = "CONFIRMED";
        Long businessId = 2L;

        orderService.confirmOrder(orderNr, status, businessId);
    }


    @Test
    public void retrieveFullOrderProfileTest() {
        String orderNr = testOrder.getOrderNr();
        String status = testOrder.getStatus().toString();
        Long businessId = testOrder.getBusinessId();
        StatusTypes confirmed = StatusTypes.CONFIRMED;

        OrderFullDao fullOrderDetails = orderService.retrieveFullOrderProfile(orderNr, status, businessId);

        assertNotNull("order profile could not be retrieved", fullOrderDetails);

        assertEquals("Name doesn't match", "Lena", fullOrderDetails.getName());
        assertEquals("Phone doesn't match", "017668558497", fullOrderDetails.getPhone());
        assertEquals("Email doesn't match", "aoneko@gmx.de", fullOrderDetails.getEMail());
        assertEquals("Date doesn't match", LocalDate.of(2018, 12, 12), fullOrderDetails.getDate());
        assertEquals("Time doesn't match", "around 1 p.m.", fullOrderDetails.getTime());
        assertEquals("Participants number doesn't match", 2L, fullOrderDetails.getParticipantsNr());
        assertEquals("Description doesn't match", "Hi, I'm Lena, will you be my guide?", fullOrderDetails.getDescription());
        assertEquals("Business id doesn't match", businessId, fullOrderDetails.getBusinessId());
        assertEquals("Status doesn't match", confirmed, fullOrderDetails.getStatus());
        assertEquals("Order number doesn't match", orderNr, fullOrderDetails.getOrderNr());
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
        StatusTypes CONFIRMED = StatusTypes.CONFIRMED;
        order.setBusinessId(1L);
        order.setStatus(CONFIRMED);
        order.setName(mockOrderNew().getName());
        order.setPhone(mockOrderNew().getPhone());
        order.setEMail(mockOrderNew().getEMail());
        order.setDate(mockOrderNew().getDate());
        order.setTime(mockOrderNew().getTime());
        order.setParticipantsNr(mockOrderNew().getParticipantsNr());
        order.setDescription(mockOrderNew().getDescription());
        return order;
    }

    private Business mockBusiness() {
        Business business = new Business();
        business.setId(1L);
        business.setFName("Ada");
        business.setLName("Polkanova");
        business.setEMail("dogsledging@doggy.de");
        business.setPhone("017668558497");
        business.setDescription("Hi, I'm Ada, the dog");
        business.setServiceLocation("berlin");
        business.setUsername("polkaner");
        business.setPassword("bones");
        business.setPhoto(null);
        return business;
    }
}