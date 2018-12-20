package de.berlin.lostberlin.service;

import de.berlin.lostberlin.exception.OperationForbiddenException;
import de.berlin.lostberlin.exception.ResourceConflictException;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.exception.ResourceNotSavedException;
import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.ChosenBusiness;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.repository.BusinessRepository;
import de.berlin.lostberlin.repository.ChosenBusinessRepository;
import de.berlin.lostberlin.repository.OrderRepository;
import de.berlin.lostberlin.service.mail.MailService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private BusinessRepository businessRepo;
    @Autowired
    private ChosenBusinessRepository chosenBusinessRepo;
    @Autowired
    private OrderService orderService;

    @MockBean
    private MailService mailService;

    @Before
    public void setUp() {
        orderService = new OrderServiceImpl(orderRepo, businessRepo, chosenBusinessRepo, mailService);
    }

    @Test
    public void retrieveAllOrdersTest() {
        orderService.retrieveAllOrders();
        List<Order> orderList = orderRepo.findAll();

        assertFalse("List of order is empty", orderList.isEmpty());

        assertEquals("Name of the first object in the list is missing", "Lena", orderList.get(8).getName());
        assertEquals("Phone of the first object in the list is missing", "017668558497", orderList.get(8).getPhone());
        assertEquals("Email of the first object in the list is missing", "aoneko@gmx.de", orderList.get(8).getEMail());
        assertEquals("Date of the first object in the list is missing", LocalDate.of(2018, 12, 12), orderList.get(8).getDate());
        assertEquals("Time of the first object in the list is missing", "around 1 p.m.", orderList.get(8).getTime());
        assertEquals("Participants number of the first object in the list is missing", 2L, orderList.get(8).getParticipantsNr());
        assertEquals("Description of the first object in the list is missing", "Hi, I'm Lena, will you be my guide?", orderList.get(8).getDescription());
    }

    @Ignore //run only after database was dropped
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveAllOrdersNotFoundTest() {
        orderService.retrieveAllOrders();
    }

    @Test
    public void retrieveOrdersByBusinessIdTest() {
        Long id = 1L;

        List<OrderShortDao> orderList = orderService.retrieveOrdersByBusinessId(id);

        assertFalse("List of order is empty", orderList.isEmpty());

        assertEquals("Name of the first object in the list is missing", "Lena", orderList.get(3).getName());
        assertEquals("Date of the first object in the list is missing", LocalDate.of(2018, 12, 12), orderList.get(3).getDate());
        assertEquals("Time of the first object in the list is missing", "around 1 p.m.", orderList.get(3).getTime());
        assertEquals("Participants number of the first object in the list is missing", 2L, orderList.get(3).getParticipantsNr());
        assertEquals("Description of the first object in the list is missing", "Hi, I'm Lena, will you be my guide?", orderList.get(3).getDescription());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void retrieveOrdersByBusinessIdNotFoundTest() {
        Long id = 12L;
        orderService.retrieveOrdersByBusinessId(id);
    }

    @Test
    public void retrieveOrderByOrderNrTest() {
        String orderNr = "2018-11_00452";

        OrderStatusDao orderStatus = orderService.retrieveOrderByOrderNr(orderNr);
        StatusTypes pending = StatusTypes.PENDING;

        assertEquals("Name is missing", "Lena", orderStatus.getName());
        assertEquals("Date is missing", LocalDate.of(2018, 12, 12), orderStatus.getDate());
        assertEquals("Time is missing", "around 1 p.m.", orderStatus.getTime());
        assertEquals("Business id is missing", null, orderStatus.getBusinessId());
        assertEquals("Status is missing", pending, orderStatus.getStatus());
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

        assertEquals("Name couldn't be saved", "Lena", savedOrder.getName());
        assertEquals("Phone couldn't be saved", "017668558497", savedOrder.getPhone());
        assertEquals("Email couldn't be saved", "aoneko@gmx.de", savedOrder.getEMail());
        assertEquals("Date couldn't be saved", LocalDate.of(2018, 12, 12), savedOrder.getDate());
        assertEquals("Time couldn't be saved", "around 1 p.m.", savedOrder.getTime());
        assertEquals("Participants number couldn't be saved", 2L, savedOrder.getParticipantsNr());
        assertEquals("Description couldn't be saved", "Hi, I'm Lena, will you be my guide?", savedOrder.getDescription());
    }

    @Ignore // Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void saveOrderProfileNotSavedTest() {
        OrderPostDto orderNew = mockOrderNew();
        orderService.saveOrderProfile(orderNew);
    }

    @Test
    public void saveChosenBusinessesTest() {
        OrderPostDto orderNew = mockOrderNew();
        ChosenBusiness savedBusiness = orderService.saveChosenBusinesses(orderNew);

        assertFalse(savedBusiness.getOrderNr().isEmpty());
        assertFalse(savedBusiness.getBusinessId().equals(null));
    }

    @Ignore // Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void aveChosenBusinessesNotSavedTest() {
        OrderPostDto orderNew = mockOrderNew();
        orderService.saveChosenBusinesses(orderNew);
    }

    @Test
    public void updateOrderStatusToPendingTest() {
        String orderNr = "2018-11_00502";
        String status = "PENDING";
        Long businessId = null;

        StatusTypes pending = StatusTypes.PENDING;

        orderService.updateOrderStatus(orderNr, status, businessId);
        Optional<Order> order = orderRepo.findById(orderNr);

        assertEquals("Status couldn't be updated", pending, order.get().getStatus());
        assertEquals("Business id wasn't set to null", null, order.get().getBusinessId());
    }

    @Test
    public void updateOrderStatusToClosedTest() {
        String orderNr = "2018-11_00402";
        String status = "CLOSED";
        Long businessId = 1L;

        StatusTypes closed = StatusTypes.CLOSED;

        orderService.updateOrderStatus(orderNr, status, businessId);
        Optional<Order> order = orderRepo.findById(orderNr);

        assertEquals("Status couldn't be updated", closed, order.get().getStatus());
        assertEquals("Business id couldn't be updated", businessId, order.get().getBusinessId());
    }

    @Test(expected = OperationForbiddenException.class)
    public void updateOrderStatusToClosedForbiddenTest() {
        String orderNr = "2018-11_00402";
        String status = "CLOSED";
        Long businessId = 1L;

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
        String orderNr = "2018-11_00502";
        String status = "CONFIRMED";
        Long businessId = 1L;

        StatusTypes confirmed = StatusTypes.CONFIRMED;

        OrderFullDao confirmedOrder = orderService.confirmOrder(orderNr, status, businessId);

        assertEquals("Status couldn't be updated", confirmed, confirmedOrder.getStatus());
        assertEquals("Business id couldn't be updated", businessId, confirmedOrder.getBusinessId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void confirmOrderNotFoundTest() {
        String orderNr = "2018-11_00001";
        String status = "CONFIRMED";
        Long businessId = 1L;

        orderService.confirmOrder(orderNr, status, businessId);
    }

    @Ignore // Run with deactivated DB
    @Test(expected = ResourceNotSavedException.class)
    public void confirmOrderNotSavedTest() {
        String orderNr = "2018-11_00502";
        String status = "CONFIRMED";
        Long businessId = 1L;

        orderService.confirmOrder(orderNr, status, businessId);
    }

    @Test(expected = ResourceConflictException.class)
    public void confirmOrderConflictTest() {
        String orderNr = "2018-11_00502";
        String status = "CONFIRMED";
        Long businessId = 2L;

        orderService.confirmOrder(orderNr, status, businessId);
    }


    @Test
    public void retrieveFullOrderProfileTest() {
        String orderNr = "2018-11_00502";
        String status = "CONFIRMED";
        Long businessId = 1L;

        StatusTypes confirmed = StatusTypes.CONFIRMED;

        OrderFullDao fullOrderDetails = orderService.retrieveFullOrderProfile(orderNr, status, businessId);

        assertNotNull("order profile could not be retrieved", fullOrderDetails);

        assertEquals("Name is missing", "Lena", fullOrderDetails.getName());
        assertEquals("Phone is missing", "017668558497", fullOrderDetails.getPhone());
        assertEquals("Email is missing", "aoneko@gmx.de", fullOrderDetails.getEMail());
        assertEquals("Date is missing", LocalDate.of(2018, 12, 12), fullOrderDetails.getDate());
        assertEquals("Time is missing", "around 1 p.m.", fullOrderDetails.getTime());
        assertEquals("Participants number is missing", 2L, fullOrderDetails.getParticipantsNr());
        assertEquals("Description is missing", "Hi, I'm Lena, will you be my guide?", fullOrderDetails.getDescription());
        assertEquals("Business id is missing", businessId, fullOrderDetails.getBusinessId());
        assertEquals("Status is missing", confirmed, fullOrderDetails.getStatus());
        assertEquals("Order number is missing", orderNr, fullOrderDetails.getOrderNr());
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
}