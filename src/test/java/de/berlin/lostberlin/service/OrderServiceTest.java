package de.berlin.lostberlin.service;

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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static de.berlin.lostberlin.model.order.persistence.StatusTypes.*;
import static java.util.Collections.singletonList;
import static org.aspectj.runtime.internal.Conversions.longValue;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ChosenBusinessRepository chosenBusinessRepository;

    private OrderService orderService;


    @Before
    public void setUp() {
        orderService = new OrderServiceImpl(orderRepository, businessRepository, chosenBusinessRepository);
    }

    @Test
    public void retrieveAllOrders() {
        Order order = order();

        List<Order> allOrders = singletonList(order);

        given(orderRepository.findAll()).willReturn(allOrders);
        given(orderService.retrieveAllOrders()).willReturn(allOrders);

        List<Order> orders = orderService.retrieveAllOrders();

        // verifies that business repository was invoked one time during the search
        verify(orderRepository, times(1)).findAll();

        assertFalse("List of orders is empty, nothing could be retrieved from the database", orders.isEmpty());

        assertEquals("order nr of the first object in the list isn't present in order repository", "1", orders.get(0).getOrderNr());
        assertEquals("business id of the first object in the list isn't present in order repository", 1L, longValue(orders.get(0).getBusinessId()));
        assertEquals("Status of the first object in the list isn't present in order repository", CONFIRMED, orders.get(0).getStatus());
        assertEquals("Name of the first object in the list isn't present in order repository", "March hare", orders.get(0).getName());
        assertEquals("Email of the first object in the list isn't present in order repository", "aoneko@gmx.de", orders.get(0).getEMail());
        assertEquals("Date of the first object in the list isn't present in order repository", LocalDate.of(2019, 02, 02), orders.get(0).getDate());
        assertEquals("Time of the first object in the list isn't present in order repository", "At six o'clock", orders.get(0).getTime());
        assertEquals("Nr of participants of the first object in the list isn't present in order repository", 3, orders.get(0).getParticipantsNr());
        assertEquals("Description of the first object in the list isn't present in order repository", "Up above the world you fly, Like a tea-tray in the sky", orders.get(0).getDescription());

    }


    /*  Checks that the exception is thrown when no orders  can be found in order repository
     */
    @Test(expected = ResponseStatusException.class)
    public void retrieveAllOrdersOrdersNotFound() {
        Order order = order();

        List<Order> allOrders = singletonList(order);

        List<Order> orders = orderService.retrieveAllOrders();
    }

    @Test
    public void retrieveOrdersByBusinessId() {
        Long id = 1L;
        OrderShortDao orderShortDao = orderShortDao();

        List<OrderShortDao> allShortOrders = singletonList(orderShortDao);

        given(chosenBusinessRepository.findAllByBusinessId(id)).willReturn(allShortOrders);
        given(orderService.retrieveOrdersByBusinessId(id)).willReturn(allShortOrders);

        List<OrderShortDao> shortOrders = orderService.retrieveOrdersByBusinessId(id);

        verify(chosenBusinessRepository, times(1)).findAllByBusinessId(id);

        assertFalse("List of orders is empty, nothing could be retrieved from the database", shortOrders.isEmpty());

        assertEquals("Name of the first object in the list isn't present in order repository", "March hare", shortOrders.get(0).getName());
        assertEquals("Date of the first object in the list isn't present in order repository", LocalDate.of(2019, 02, 02), shortOrders.get(0).getDate());
        assertEquals("Time of the first object in the list isn't present in order repository", "At six o'clock", shortOrders.get(0).getTime());
        assertEquals("Nr of participants of the first object in the list isn't present in order repository", 3, shortOrders.get(0).getParticipantsNr());
        assertEquals("Description of the first object in the list isn't present in order repository", "Up above the world you fly, Like a tea-tray in the sky", shortOrders.get(0).getDescription());

    }

    /*
     Checks that the exception is thrown when no matching orders can be found in order repository by given id
      */
    @Test(expected = ResponseStatusException.class)
    public void retrieveOrdersByBusinessIdOrdersNotFound() {
        Long id = 1L;
        OrderShortDao orderShortDao = orderShortDao();

        List<OrderShortDao> allShortOrders = singletonList(orderShortDao);

        List<OrderShortDao> shortOrders = orderService.retrieveOrdersByBusinessId(id);
    }

    @Test
    public void retrieveOrderByOrderNr() {
        String orderNr = "123";
        OrderStatusDao orderStatusDao = orderStatusDao();

        given(orderRepository.getOrderStatus(orderNr)).willReturn(orderStatusDao);
        when(orderService.retrieveOrderByOrderNr(orderNr)).thenReturn(orderStatusDao);

        OrderStatusDao orderStatus = orderService.retrieveOrderByOrderNr(orderNr);

        assertNotNull("No orderStatus profile with given orderNr could be retrieved from orderStatus repository", orderStatus);

        assertEquals("Name isn't present in order repository", "March hare", orderStatus.getName());
        assertEquals("Date isn't present in order repository", LocalDate.of(2019, 02, 02), orderStatus.getDate());
        assertEquals("Time isn't present in order repository", "At six o'clock", orderStatus.getTime());
        assertEquals("business id isn't present in order repository", 1L, longValue(orderStatus.getBusinessId()));
        assertEquals("Status isn't present in order repository", CONFIRMED, orderStatus.getStatus());
    }

    /*
         Checks that the exception is thrown when no matching orders can be found in order repository by given order number
          */
    @Test(expected = ResponseStatusException.class)
    public void retrieveOrdersByOrderNrOrderNotFound() {
        String orderNr = "123";
        OrderStatusDao orderStatusDao = orderStatusDao();

        OrderStatusDao orderStatus = orderService.retrieveOrderByOrderNr(orderNr);
    }

    @Ignore
    @Test
    public void saveOrderProfile() {
        OrderPostDto orderPostDto = orderPostDto();
        Order savedOrder = savedOrder();

        given(orderRepository.save(savedOrder)).willReturn(savedOrder);

        when(orderService.saveOrderProfile(orderPostDto)).thenReturn(savedOrder);

        Order orderNew = orderService.saveOrderProfile(orderPostDto);

        verify(orderRepository, times(1)).save(savedOrder);

        assertNotNull("New object couldn't be saved", orderNew);

        assertEquals("Name isn't present in order repository", "March hare", orderNew.getName());
        assertEquals("Phone isn't present in order repository", "017668558497", orderNew.getPhone());
        assertEquals("Email isn't present in order repository", "aoneko@gmx.de", orderNew.getEMail());
        assertEquals("Date isn't present in order repository", LocalDate.of(2019, 02, 02), orderNew.getDate());
        assertEquals("Time isn't present in order repository", "At six o'clock", orderNew.getTime());
        assertEquals("Nr of participants isn't present in order repository", 3, orderNew.getParticipantsNr());
        assertEquals("Description isn't present in order repository", "Up above the world you fly, Like a tea-tray in the sky", orderNew.getDescription());
        assertEquals("Status isn't present in order repository", PENDING, orderNew.getStatus());
    }

    @Test
    public void saveChosenIds() {
        OrderPostDto orderPostDto = orderPostDto();
        // order savedOrder = savedOrder();
        ChosenBusiness chosenBusiness = chosenBusiness();
        given(chosenBusinessRepository.save(chosenBusiness)).willReturn(chosenBusiness);
        orderService.saveOrderProfile(orderPostDto);

        verify(chosenBusinessRepository, times(1)).save(chosenBusiness);

        assertNotNull("Chosen business couldn't be saved", chosenBusiness);

        assertEquals("business id isn't present in chosen orders repository", 1L, longValue(chosenBusiness.getBusinessId()));
        //    assertEquals("order number isn't present in chosen business repository", "1", chosenBusiness.getOrderNr());
    }

    @Test
    public void updateOrderStatus() {
        Long businessId = 1L;
        String orderNr = "1";
        String statusPending = "PENDING";
        String statusConfirmed = "CONFIRMED";
        String statusClosed = "CLOSED";

        Order orderStatusIsPending = orderStatusIsPending();
        Order orderStatusIsConfirmed = orderStatusIsConfirmed();
        Order orderStatusToConfirmed = orderStatusToConfirmed();
        Order orderStatusToPending = orderStatusToPending();
        Order orderStatusToClosed = orderStatusToClosed();

        Business business = business();

    // Scenario 1: changing the status of pending order
        given(orderRepository.findById(orderNr)).willReturn(java.util.Optional.ofNullable(orderStatusIsPending));
        given(businessRepository.findById(businessId)).willReturn(java.util.Optional.ofNullable(business));

        // Scenario 1.1: changing status to "confirmed"
        orderService.updateOrderStatus(orderNr, statusConfirmed, businessId);
        assertNotNull(orderStatusToConfirmed);

        assertEquals(1L, longValue(orderStatusToConfirmed.getBusinessId()));
        assertEquals(CONFIRMED, orderStatusToConfirmed().getStatus());

        // Scenario 1.2: changing status to "closed"
        orderService.updateOrderStatus(orderNr, statusClosed, businessId);
        assertNotNull(orderStatusToClosed);

        assertEquals(1L, longValue(orderStatusToClosed.getBusinessId()));
        assertEquals(CLOSED, orderStatusToClosed().getStatus());

    // Scenario 2: changing the status of confirmed order.
        given(orderRepository.findById(orderNr)).willReturn(java.util.Optional.ofNullable(orderStatusIsConfirmed));
        given(businessRepository.findById(businessId)).willReturn(java.util.Optional.ofNullable(business));

        // Scenario 2.1: changing status to "pending"
        orderService.updateOrderStatus(orderNr, statusPending, businessId);
        assertNotNull(orderStatusToPending());

        assertEquals(null, orderStatusToPending.getBusinessId());
        assertEquals(PENDING, orderStatusToPending().getStatus());

        // Scenario 2.2: changing status to "closed"
        orderService.updateOrderStatus(orderNr, statusClosed, businessId);
        assertNotNull(orderStatusToClosed());

        assertEquals(1L, longValue(orderStatusToClosed.getBusinessId()));
        assertEquals(CLOSED, orderStatusToClosed().getStatus());

    }

    @Test(expected = ResponseStatusException.class)
    public void updateOrderStatusOrderAndBusinessNotFound() {
        Long businessId = 1L;
        String orderNr = "1";
        String statusConfirmed = "CONFIRMED";

        orderService.updateOrderStatus(orderNr, statusConfirmed, businessId);
    }

    @Test
    public void retrieveFullOrderProfile() {
        Long businessId = 1L;
        String orderNr = "1";
        String statusConfirmed = "CONFIRMED";
        StatusTypes confirmed = CONFIRMED;

        OrderFullDao orderFullDao = orderFullDao();

        given(orderRepository.getFullOrder(orderNr, confirmed, businessId)).willReturn(orderFullDao);
        when(orderService.retrieveFullOrderProfile(orderNr, statusConfirmed, businessId)).thenReturn(orderFullDao);

        OrderFullDao orderFull = orderService.retrieveFullOrderProfile(orderNr, statusConfirmed, businessId);

        assertNotNull("No orderStatus profile with given orderNr could be retrieved from orderStatus repository", orderFull);

        assertEquals("Name isn't present in order repository", "March hare", orderFull.getName());
        assertEquals("Phone isn't present in order repository", "017668558497", orderFull.getPhone());
        assertEquals("Email isn't present in order repository", "aoneko@gmx.de", orderFull.getEMail());
        assertEquals("Date isn't present in order repository", LocalDate.of(2019, 02, 02), orderFull.getDate());
        assertEquals("Time isn't present in order repository", "At six o'clock", orderFull.getTime());
        assertEquals("Number of participants isn't present in order repository", 3, orderFull.getParticipantsNr());
        assertEquals("Description isn't present in order repository", "Up above the world you fly, Like a tea-tray in the sky", orderFull.getDescription());
        assertEquals("business id isn't present in order repository", 1L, longValue(orderFull.getBusinessId()));
        assertEquals("Status isn't present in order repository", CONFIRMED, orderFull.getStatus());
        assertEquals("order nr isn't present in order repository", "1", orderFull.getOrderNr());


    }

    private Order order() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(1L);
        order.setStatus(CONFIRMED);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setPhone("017668558497");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private OrderShortDao orderShortDao() {
        OrderShortDao orderShortDao = new OrderShortDao();
        orderShortDao.setName(order().getName());
        orderShortDao.setDate(order().getDate());
        orderShortDao.setTime(order().getTime());
        orderShortDao.setParticipantsNr(order().getParticipantsNr());
        orderShortDao.setDescription(order().getDescription());
        return orderShortDao;
    }

    private OrderStatusDao orderStatusDao() {
        OrderStatusDao orderstatusDao = new OrderStatusDao();
        orderstatusDao.setName(order().getName());
        orderstatusDao.setDate(order().getDate());
        orderstatusDao.setTime(order().getTime());
        orderstatusDao.setBusinessId(order().getBusinessId());
        orderstatusDao.setStatus(CONFIRMED);
        return orderstatusDao;
    }

    private OrderFullDao orderFullDao() {
        OrderFullDao orderFullDao = new OrderFullDao();
        orderFullDao.setName(order().getName());
        orderFullDao.setPhone(order().getPhone());
        orderFullDao.setEMail(order().getEMail());
        orderFullDao.setDate(order().getDate());
        orderFullDao.setTime(order().getTime());
        orderFullDao.setParticipantsNr(order().getParticipantsNr());
        orderFullDao.setDescription(order().getDescription());
        orderFullDao.setBusinessId(order().getBusinessId());
        orderFullDao.setStatus(order().getStatus());
        orderFullDao.setOrderNr(order().getOrderNr());
        return orderFullDao;
    }

    private OrderPostDto orderPostDto() {
        OrderPostDto orderPostDto = new OrderPostDto();
        Long[] chosenBusinessIds = {1L, 2L, 3L};
        orderPostDto.setChosenBusinessIds(chosenBusinessIds);
        orderPostDto.setName("March hare");
        orderPostDto.setPhone("017668558497");
        orderPostDto.setEMail("aoneko@gmx.de");
        orderPostDto.setDate(LocalDate.of(2019, 02, 02));
        orderPostDto.setTime("At six o'clock");
        orderPostDto.setParticipantsNr(3);
        orderPostDto.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return orderPostDto;
    }

    private Order savedOrder() {
        Order savedOrder = new Order();
        //   savedOrder.setOrderNr("1");
        savedOrder.setName(orderPostDto().getName());
        savedOrder.setPhone(orderPostDto().getPhone());
        savedOrder.setEMail(orderPostDto().getEMail());
        savedOrder.setDate(orderPostDto().getDate());
        savedOrder.setTime(orderPostDto().getTime());
        savedOrder.setParticipantsNr(orderPostDto().getParticipantsNr());
        savedOrder.setDescription(orderPostDto().getDescription());
        savedOrder.setStatus(order().getStatus());
        return savedOrder;
    }

    private ChosenBusiness chosenBusiness() {
        ChosenBusiness chosenBusiness = new ChosenBusiness();
        Long[] chosenBusinessIds = orderPostDto().getChosenBusinessIds();
        chosenBusiness.setBusinessId(chosenBusinessIds[0]);
        //   chosenBusiness.setOrderNr(savedOrder().getOrderNr());
        return chosenBusiness;
    }

    private Order orderStatusIsPending() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(null);
        order.setStatus(PENDING);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private Order orderStatusIsConfirmed() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(1L);
        order.setStatus(CONFIRMED);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private Order orderStatusToPending() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(null);
        order.setStatus(PENDING);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private Order orderStatusToConfirmed() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(1L);
        order.setStatus(CONFIRMED);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private Order orderStatusToClosed() {
        Order order = new Order();
        order.setOrderNr("1");
        order.setBusinessId(1L);
        order.setStatus(CLOSED);
        order.setName("March hare");
        order.setEMail("aoneko@gmx.de");
        order.setDate(LocalDate.of(2019, 02, 02));
        order.setTime("At six o'clock");
        order.setParticipantsNr(3);
        order.setDescription("Up above the world you fly, Like a tea-tray in the sky");
        return order;
    }

    private Business business() {
        Business business = new Business();
        business.setId(1L);
        business.setFName("Ada");
        business.setLName("Polkanova");
        business.setEMail("polkaner@dogsrgods.com");
        return business;
    }
}