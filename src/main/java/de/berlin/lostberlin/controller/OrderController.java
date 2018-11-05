package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.exception.ResourceNotFoundException;
import de.berlin.lostberlin.model.Order;
import de.berlin.lostberlin.repository.OrderRepository;
import de.berlin.lostberlin.service.mail.MailService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private MailService mailService;

    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @GetMapping("/orders")

    public List<Order> getOrders(@RequestParam (required=true, value = "business_id") Long businessId) {

        return orderRepo.findAllByBusinessId(businessId);
    }

    @PostMapping("/orders")

    public String createOrder(@Valid @RequestBody Order order){
        if (order.getChosenBusinessIds().length==0
                ||order.getName().equals("")
                ||!EmailValidator.getInstance().isValid(order.getEmail())
        ){

            return "Bad request";
        }
        Order result = orderRepo.save(order);
        if (result != null) {
            try {
                mailService.sendConfirmationMail(result);
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "Failed to save order " + order.getOrderNr();
        }
        return "Success";
    }

    @GetMapping("/orders/{order_number}")
    public Order getByOrderNr(@PathVariable(value = "order_number") String orderNr) {
        return orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));
    }

    @PutMapping("/orders/{order_number}/status")

    public Order updateOrderStatus(@PathVariable (value="order_number") String orderNr, @Valid @RequestBody Order orderProfile){
    Order order = orderRepo.findById(orderNr)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "order_number", orderNr));
    order.setBusinessId(orderProfile.getBusinessId());
    order.setStatus(orderProfile.getStatus());


        Order result = orderRepo.save(order);

        if(result !=null && result.getStatus().equals("confirmed")){
            try {
                mailService.sendNotificationMail(result);
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return result;
    }

}
