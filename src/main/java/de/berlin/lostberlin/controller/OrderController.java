package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.service.MailService;
import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.OrderRepository;
import org.apache.commons.validator.routines.EmailValidator;
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
    public String createOrder(@Valid @RequestBody Order order){
        if (order.getChosenBusinessIds().length==0
                ||order.getName().equals("")
                ||!EmailValidator.getInstance().isValid(order.getEmail())
        ){
            return "Bad parameters";
        }
        Order result = orderRepo.save(order);
        if (result!=null){

            try {
                MailService.sendEmail(result, MailService.MailTypes.CONFIRMATION);
            } catch (Exception e) {
                return e.getMessage();
            }

        }else {
            return "Failed to save order "+order.getOrderNr();
        }
        return "Success";
    }

    @GetMapping("/orders/{order_number}")
    public Order getByOrderNr(@PathVariable (value="order_number") String orderNr){
        return orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));
    }

    @PutMapping("/orders/{order_number}/status")
    public Order updateOrderStatus(@PathVariable (value="order_number") String orderNr, @Valid @RequestBody Order orderProfile){
    Order order = orderRepo.findById(orderNr)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));
    order.setStatus(orderProfile.getStatus());

        return orderRepo.save(order);
    }

}
