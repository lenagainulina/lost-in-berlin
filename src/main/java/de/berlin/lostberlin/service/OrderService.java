package de.berlin.lostberlin.service;

import de.berlin.lostberlin.model.order.client.OrderFullDao;
import de.berlin.lostberlin.model.order.client.OrderPostDto;
import de.berlin.lostberlin.model.order.client.OrderShortDao;
import de.berlin.lostberlin.model.order.client.OrderStatusDao;
import de.berlin.lostberlin.model.order.persistence.Order;

import java.util.List;

public interface OrderService {

    /**
     * Gets all orders from the repository for administration purposes (not accessible by businesses or clients)
     *
     * @return list of all {@link Order} objects saved in order repository
     */
    List<Order> retrieveAllOrders();

    /**
     * Gets all orders by given business id. Given id is matched to the ids of businesses,
     * previously chosen by the client.
     *
     * @param businessId id of a business, performing the GET request
     * @return list of {@link OrderShortDao} objects matching given business id, which contain brief order details
     * (contact information and internal data is excluded)
     */
    List<OrderShortDao> retrieveOrdersByBusinessId(Long businessId);

    /**
     * Gets a short version of order profile by given order number, which can be retrieved by a client
     * to check the order status.
     *
     * @param orderNr order number by which it is identified in order repository
     * @return {@link OrderStatusDao} object matching given order number, which contains brief order details
     * and information on the current order status.
     */
    OrderStatusDao retrieveOrderByOrderNr(String orderNr);

    /**
     * Saves a newly created order profile to order repository
     *
     * @param orderProfile newly created order profile
     * @return {@link Order} object with entered order profile details
     */
    Order saveOrderProfile (OrderPostDto orderProfile);

    /**
     * Saves an order profile with an updated status and assigned business id to order repository
     *
     * @param orderNr order number by which it is identified in order repository
     * @param businessId id of a business, associated with a newly updated status
     * @param status newly updated order status
     */
    void updateOrderStatus (String orderNr, String status, Long businessId);

    /**
     * Gets a full order profile by given order number, order status and business id, assigned to the order.
     *
     * @param orderNr order number by which it is identified in order repository
     * @param status id of a business, associated with a newly updated status
     * @param businessId newly updated order status
     * @return {@link OrderFullDao} object with all order profile details, updated status and assigned business id (internal data is excluded)
     */
    OrderFullDao retrieveFullOrderProfile (String orderNr, String status, Long businessId);
}
