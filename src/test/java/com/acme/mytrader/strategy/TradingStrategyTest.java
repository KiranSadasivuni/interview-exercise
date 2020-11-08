package com.acme.mytrader.strategy;

import com.acme.mytrader.order.Order;
import com.acme.mytrader.order.OrderStatus;
import com.acme.mytrader.order.Side;
import com.db.OrderCache;
import com.db.OrderCacheImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class TradingStrategyTest {
    TradingStrategy underTest = new TradingStrategy();
    OrderCache cache;

    @BeforeEach
    void setUp() {
        cache = OrderCacheImpl.INSTANCE;
    }

    @AfterEach
    void tearDown() {
        cache = null;
    }

    private void generateDataForOneSecurity() {
        Order order = new Order("IBM", "Trader1", 55.0, 100, Side.BUY, OrderStatus.PRESET);

        ConcurrentMap<String, List<Order>> newOrders = new ConcurrentHashMap<>();
        newOrders.put("IBM", new ArrayList<>(Arrays.asList(order)));
        cache.addOrders(newOrders);
    }

    private void generateDataForTwoSecurities() {
        Order order1 = new Order("IBM", "Trader1", 55.0, 100, Side.BUY, OrderStatus.PRESET);
        Order order2 = new Order("APPLE", "Trader2", 100.12, 100, Side.BUY, OrderStatus.PRESET);

        ConcurrentMap<String, List<Order>> newOrders = new ConcurrentHashMap<>();
        newOrders.put("IBM", new ArrayList<>(Arrays.asList(order1)));
        newOrders.put("APPLE", new ArrayList<>(Arrays.asList(order2)));
        cache.addOrders(newOrders);
    }

    private void generateDataForNewOrderNotImpacted() {
        Order order1 = new Order("IBM", "Trader1", 55.0, 100, Side.BUY, OrderStatus.NEW);
        Order order2 = new Order("IBM", "Trader2", 55.0, 50, Side.BUY, OrderStatus.PRESET);

        ConcurrentMap<String, List<Order>> newOrders = new ConcurrentHashMap<>();
        newOrders.put("IBM", new ArrayList<>(Arrays.asList(order1, order2)));
        cache.addOrders(newOrders);
    }

    @Test
    void marketPriceMatchesForOneSecurity() {

        generateDataForOneSecurity();

        underTest.applyIfValid("IBM", 55.0);

        Order testOrder = cache.getOrders("IBM").get(0);
        assertEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
    }

    @Test
    void marketPriceNotMatchesForOneSecurity() {

        generateDataForOneSecurity();

        underTest.applyIfValid("IBM", 56.0);

        Order testOrder = cache.getOrders("IBM").get(0);
        assertNotEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
    }

    @Test
    void marketPriceMatchesForTwoSecuritiesConcurrentMode() {

        generateDataForTwoSecurities();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                underTest.applyIfValid("IBM", 55.0);
                Order testOrder = cache.getOrders("IBM").get(0);
                assertEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
                return 0;
            }
        });
        executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                underTest.applyIfValid("APPLE", 100.0);
                Order testOrder = cache.getOrders("APPLE").get(0);
                assertEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
                return 0;
            }
        });
    }

    @Test
    void marketPriceNotMatchesForTwoSecuritiesConcurrentMode() {

        generateDataForTwoSecurities();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                underTest.applyIfValid("IBM", 57.0);
                Order testOrder = cache.getOrders("IBM").get(0);
                assertEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
                return 0;
            }
        });
        executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                underTest.applyIfValid("APPLE", 102.0);
                Order testOrder = cache.getOrders("APPLE").get(0);
                assertEquals(OrderStatus.PLACED, testOrder.getOrderStatus());
                return 0;
            }
        });
    }

    @Test
    void marketPriceMatchesNewOrderNotImpactedForOneSecurity() {

        generateDataForNewOrderNotImpacted();

        underTest.applyIfValid("IBM", 55.0);

        Order newOrder = null;
        Order presetOrder = null;
        List<Order> orders = cache.getOrders("IBM");

        assertNotNull(orders);

        for(Order order : orders) {
            if (order.getPlacedBy().equals("Trader1")) {
                newOrder = order;
            } else {
                presetOrder = order;
            }
        };

        assertNotNull(newOrder);
        assertNotNull(presetOrder);
        assertEquals(OrderStatus.NEW, newOrder.getOrderStatus());
        assertEquals(OrderStatus.PLACED, presetOrder.getOrderStatus());
    }
}
