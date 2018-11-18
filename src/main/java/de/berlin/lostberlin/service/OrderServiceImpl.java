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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepo;
    private BusinessRepository businessRepo;
    private ChosenBusinessRepository chosenBusinessRepo;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepo, BusinessRepository businessRepo, ChosenBusinessRepository chosenBusinessRepo) {
        this.orderRepo = orderRepo;
        this.businessRepo = businessRepo;
        this.chosenBusinessRepo= chosenBusinessRepo;
    }

    @Override
    public List<Order> retrieveAllOrders() {

        List<Order> orderList = orderRepo.findAll();

        if(orderList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found");
        }
        return orderList;
    }

    @Override
    public List<OrderShortDao> retrieveOrdersByBusinessId(Long businessId) {
        List<OrderShortDao> orderShortDaoList = chosenBusinessRepo.findAllByBusinessId(businessId);
        if (orderShortDaoList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders assigned to this business id");
        }
        return orderShortDaoList;
    }

    @Override
    public OrderStatusDao retrieveOrderByOrderNr(String orderNr) {
        OrderStatusDao result = orderRepo.getOrderStatus(orderNr);
        if (result==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found");
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

        Long[] chosenBusinessIds = orderProfile.getChosenBusinessIds();
        for(Long id:chosenBusinessIds) {
            ChosenBusiness chosenBusiness = new ChosenBusiness();
            chosenBusiness.setBusinessId(id);
            chosenBusiness.setOrderNr(order.getOrderNr());
            chosenBusinessRepo.save(chosenBusiness);
        }
        return order;
    }

    @Override
    public void updateOrderStatus(String orderNr, String status, Long businessId) {
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));

                if (status.equals("PENDING")) {
                    order.setBusinessId(null);
                    }
                        else {
                    Business business = businessRepo.findById(businessId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "business not found"));
                    order.setBusinessId(businessId);
                }

            order.setStatus(StatusTypes.valueOf(status));
        orderRepo.save(order);
    }

    @Override
    public OrderFullDao retrieveFullOrderProfile(String orderNr, String status, Long businessId) {
        return orderRepo.getFullOrder(orderNr, StatusTypes.valueOf(status), businessId);
    }
}
