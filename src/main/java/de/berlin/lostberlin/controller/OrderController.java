package de.berlin.lostberlin.controller;

import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.Order;
import de.berlin.lostberlin.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok().body(orderService.retrieveAllOrders());
    }

    @GetMapping
    public ResponseEntity<List<OrderShortDao>> getOrders(@RequestParam(value = "business_id") Long businessId) {
        return ResponseEntity.ok().body(orderService.retrieveOrdersByBusinessId(businessId));
    }

    @GetMapping("/{order_number}")
    public ResponseEntity<OrderStatusDao> getByOrderNr(@PathVariable(value = "order_number") String orderNr) {
        return ResponseEntity.ok().body(orderService.retrieveOrderByOrderNr(orderNr));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderPostDto orderProfile) {
        Order savedOrder = orderService.saveOrderProfile(orderProfile);
        orderService.saveChosenBusinesses(orderProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PutMapping("/{order_number}/confirmation")
    public ResponseEntity<OrderFullDao> confirmOrder(
            @PathVariable(value = "order_number") String orderNr,
            @Valid @RequestParam String status,
            @Valid @RequestParam(value = "business_id") Long businessId
    ) {
        return ResponseEntity.accepted().body(orderService.confirmOrder(orderNr, status, businessId));
    }

    @PutMapping("/{order_number}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable(value = "order_number") String orderNr,
            @Valid @RequestParam String status,
            @Valid @RequestParam(required = false, value = "business_id") Long businessId) {
        orderService.updateOrderStatus(orderNr, status, businessId);
        return ResponseEntity.ok("The status of your order has been successfully changed to " + status);
    }
}