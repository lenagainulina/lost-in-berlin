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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepo;
    private BusinessRepository businessRepo;
    private ChosenBusinessRepository chosenBusinessRepo;
    private MailService mailService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepo, BusinessRepository businessRepo, ChosenBusinessRepository chosenBusinessRepo, MailService mailService) {
        this.orderRepo = orderRepo;
        this.businessRepo = businessRepo;
        this.chosenBusinessRepo = chosenBusinessRepo;
        this.mailService = mailService;
    }

    @Override
    public List<Order> retrieveAllOrders() {

        List<Order> orderList = orderRepo.findAll();

        if (orderList.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }
        return orderList;
    }

    @Override
    public List<OrderShortDao> retrieveOrdersByBusinessId(Long businessId) {
        List<OrderShortDao> orderShortDaoList = chosenBusinessRepo.findAllByBusinessId(businessId);
        if (orderShortDaoList.isEmpty()) {
            throw new ResourceNotFoundException("No orders assigned to this business id");
        }
        return orderShortDaoList;
    }

    @Override
    public OrderStatusDao retrieveOrderByOrderNr(String orderNr) {
        OrderStatusDao result = orderRepo.getOrderStatus(orderNr);
        if (result == null) {
            throw new ResourceNotFoundException("Order not found");
        }
        return result;
    }

    @Override
    public Order saveOrderProfile(OrderPostDto orderProfile) {
        Order order = new Order();
        order.setName(orderProfile.getName());
        order.setPhone(orderProfile.getPhone());
        order.setEMail(orderProfile.getEMail());
        order.setDate(orderProfile.getDate());
        order.setTime(orderProfile.getTime());
        order.setParticipantsNr(orderProfile.getParticipantsNr());
        order.setDescription(orderProfile.getDescription());
        order.setStatus(StatusTypes.PENDING);
        orderRepo.save(order);

        mailService.sendConfirmationMail(order);

        return order;
    }

    @Override
    public ChosenBusiness saveChosenBusinesses(OrderPostDto orderProfile) {
        Order order = saveOrderProfile(orderProfile);
        Long[] chosenBusinessIds = orderProfile.getChosenBusinessIds();
        ChosenBusiness chosenBusiness = null;
        for (Long id : chosenBusinessIds) {
            chosenBusiness = new ChosenBusiness();
            chosenBusiness.setBusinessId(id);
            chosenBusiness.setOrderNr(order.getOrderNr());
            chosenBusinessRepo.save(chosenBusiness);
        }
        return chosenBusiness;
    }

    @Override
    public void updateOrderStatus(String orderNr, String status, Long businessId) {
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus().equals(StatusTypes.CLOSED)) {
            throw new OperationForbiddenException("Following order has been already closed: ", "order profile number ", orderNr);
        } else {
            if (status.equals("PENDING")) {
                order.setBusinessId(null);
            } else {
                Business business = businessRepo.findById(businessId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found", "business profile", businessId));
                order.setBusinessId(businessId);
            }
            order.setStatus(StatusTypes.valueOf(status));
            orderRepo.save(order);
        }
    }

    @Override
    public OrderFullDao confirmOrder(String orderNr, String status, Long businessId) {
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus().equals(StatusTypes.PENDING)) {
            updateOrderStatus(orderNr, status, businessId);
            OrderFullDao result = retrieveFullOrderProfile(orderNr, status, businessId);
            if (result != null) {
                try {
                    mailService.sendNotificationMail(result);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                throw new ResourceNotSavedException("Failed to save", "order profile", orderNr);
            }
            return result;
        } else {
            throw new ResourceConflictException("Already taken by another business: ", "order profile number ", orderNr);
        }
    }

    @Override
    public OrderFullDao retrieveFullOrderProfile(String orderNr, String status, Long businessId) {
        return orderRepo.getFullOrder(orderNr, StatusTypes.valueOf(status), businessId);
    }
}