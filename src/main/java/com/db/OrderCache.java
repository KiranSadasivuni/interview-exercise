package com.db;

import com.acme.mytrader.order.Order;
import com.acme.mytrader.order.OrderStatus;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>InMemory Cache for all orders.</pre>
 * <pre>Capable of handling all Reads and Writes concurrently</pre>
 */
public interface OrderCache {

    /**
     * <pre>Add Orders to the Cache.
     * Can add multiple orders for each Security </pre>
     * @param orders
     */
    void addOrders(ConcurrentMap<String, List<Order>> orders);

    /**
     * <pre>Get All Orders for a specific Security</pre>
     * @param security
     * @return Synchronised List of all Orders for the given Security.
     *         If no orders present, then returns an empty list
     */
    List<Order> getOrders(String security);

    /**
     * <pre>Update the Order Status of an Order in the Cache</pre>
     * @param order
     * @param newOrderStatus
     * @throws IllegalStateException
     */
    void updateOrderStatus(Order order, OrderStatus newOrderStatus);
}
