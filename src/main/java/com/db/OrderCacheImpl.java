package com.db;

import com.acme.mytrader.order.Order;
import com.acme.mytrader.order.OrderStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@inheritDoc}
 */
public enum OrderCacheImpl implements OrderCache {
    INSTANCE;

    private final ConcurrentMap<String, List<Order>> ordersPerSecurity = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private static final Logger logger = LogManager.getLogger(OrderCacheImpl.class);

    /**
     * {@inheritDoc}
     */
    public List<Order> getOrders(String security) {
        List<Order> ordersToReturn;
        logger.info("Finding the Orders for the Security {}", security);

        try {
            lock.lock();
            logger.debug("Lock acquired");

            List<Order> orders = ordersPerSecurity.get(security);
            if (null == orders) {
                ordersToReturn = Collections.EMPTY_LIST;
            } else {
                ordersToReturn = Collections.synchronizedList(orders);
            }
        } finally {
            lock.unlock();
            logger.debug("Lock released");
        }
        logger.info("Returning {} Orders for the Security {}", ordersToReturn.size(), security);

        return ordersToReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void updateOrderStatus(Order order, OrderStatus newOrderStatus) {
        try {
            lock.lock();
            logger.debug("Lock acquired");

            List<Order> orders = ordersPerSecurity.get(order.getSecurity());
            if (null == orders) {
                throw new IllegalStateException("Orders Cache cannot be empty at this stage");
            }

            int index = orders.indexOf(order);
            if (-1 == index) {
                throw new IllegalStateException("Order data lost in the Order Cache");
            }
            orders.get(index).setOrderStatus(newOrderStatus);
            logger.info("Status has been updated from {} to {} for the security {}", order.getOrderStatus(), newOrderStatus, order.getSecurity());

        } finally {
            lock.unlock();
            logger.debug("Lock released");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addOrders(ConcurrentMap<String, List<Order>> newOrders) {
        try {
            lock.lock();
            logger.debug("Lock acquired");

            newOrders.forEach((key, value) -> {
                ordersPerSecurity.merge(key, value, (existingOrderList, newOrderList) -> {
                    existingOrderList.addAll(newOrderList);
                    return existingOrderList;
                });
            });
        } finally {
            lock.unlock();
            logger.debug("Lock released");
        }
        logger.debug("Orders have been added to cache");
    }

    @Override
    public String toString() {
        return "OrderCache{" +
                "ordersPerSecurity=" + ordersPerSecurity +
                '}';
    }
}
