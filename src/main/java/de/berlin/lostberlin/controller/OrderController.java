package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.model.order.persistence.StatusTypes;
import de.berlin.lostberlin.repository.OrderRepository;
import de.berlin.lostberlin.service.OrderService;
import de.berlin.lostberlin.service.mail.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderRepository orderRepo;
    private MailService mailService;
    private OrderService orderService;

    public OrderController(OrderRepository orderRepo,MailService mailService, OrderService orderService) {
        this.orderRepo = orderRepo;
        this.mailService = mailService;
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.retrieveAllOrders();
    }

    @GetMapping
    public List<OrderShortDao> getOrders(@RequestParam(value = "business_id") Long businessId) {
        return orderService.retrieveOrdersByBusinessId(businessId);
    }

    @GetMapping("/{order_number}")
    public OrderStatusDao getByOrderNr(@PathVariable(value = "order_number") String orderNr) {
        return orderService.retrieveOrderByOrderNr(orderNr);
    }

    @PostMapping
    public ResponseEntity createOrder(@Valid @RequestBody OrderPostDto orderProfile) {

        Order result = orderService.saveOrderProfile(orderProfile);

        if (result != null) {
            try {
                mailService.sendConfirmationMail(result);
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save order for" + orderProfile.getName());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("order created");
    }

    @PutMapping("/{order_number}/confirmation")
    public ResponseEntity confirmOrder(
            @PathVariable(value = "order_number") String orderNr,
            @Valid @RequestParam String status,
            @Valid @RequestParam (value = "business_id") Long businessId
    ) {
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
        if (order.getStatus().equals(StatusTypes.PENDING)) {
            orderService.updateOrderStatus(orderNr, status, businessId);
            OrderFullDao result = orderService.retrieveFullOrderProfile(orderNr, status, businessId);
            if (result != null) {
            try {
                mailService.sendNotificationMail(result);
            } catch (Exception e) {
                e.getMessage();
            }
            }else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save order " + order.getOrderNr());
            }
            return ResponseEntity.accepted().body(result);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Sorry, the order has already been taken by another business");
        }
    }

    @PutMapping("/{order_number}/status")
    public ResponseEntity updateOrderStatus(
            @PathVariable(value = "order_number") String orderNr,
            @Valid @RequestParam String status,
            @Valid @RequestParam(required = false, value ="business_id") Long businessId) {
        Order order = orderRepo.findById(orderNr)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found"));
        if (!order.getStatus().equals(StatusTypes.CLOSED)) {
            orderService.updateOrderStatus(orderNr, status, businessId);
            return ResponseEntity.ok("The status of your order has been successfully changed to " + status);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Once the order has been closed, it can't be reopened");
        }
    }
}
