package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/orders/all")
    public List<Order> getAllOrders(){
        return orderRepo.findAll();
    }

    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam (required=true, value = "business_id") String businessId){
        return orderRepo.findAllByBusinessId(businessId);
    }

    @PostMapping("/orders")
    public Order createOrder(@Valid @RequestBody Order order){
        return orderRepo.save(order);
    }

    @GetMapping("/orders/{order_number}")
    public Order getByOrderNr(@PathVariable (value="order_number") String orderNr){
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));
        if (order.getBusinessId()==null){
            order.seteMail("");
            order.setPhone("");
        }
        return order;
    }

    @PutMapping("/orders/{order_number}/status")
    public Order updateOrderStatus(@PathVariable (value="order_number") String orderNr, @Valid @RequestBody Order orderProfile){
    Order order = orderRepo.findById(orderNr)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));

    order.setStatus(orderProfile.getStatus());

        return orderRepo.save(order);
    }

}
