package com.acme.mytrader.strategy;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.execution.ExecutionServiceImpl;
import com.acme.mytrader.order.Order;
import com.acme.mytrader.order.OrderStatus;
import com.db.OrderCache;
import com.db.OrderCacheImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * <pre>
 * User Story: As a trader I want to be able to monitor stock prices such
 * that when they breach a trigger level orders can be executed automatically
 * </pre>
 */
public class TradingStrategy {

    private final OrderCache orderCache = OrderCacheImpl.INSTANCE;
    private final ExecutionService executionService = new ExecutionServiceImpl();
    private static final Logger logger = LogManager.getLogger(TradingStrategy.class);

    /**
     * Triggered when marketPrice is updated for a Security.
     * Places the Order automatically if the marketPrice and Order price are as per Trader's requirement.
     * Only PRESET Orders are impacted
     *
     * @param security
     * @param marketPrice
     * @throws UnsupportedOperationException
     */
    public void applyIfValid(String security, double marketPrice) {
        logger.info("Price change notified. Applying Trading Strategy for the security {}, market price {}", security, marketPrice);

        List<Order> orders = orderCache.getOrders(security);
        orders.forEach(order -> {
            if (order.isPreset() && this.isApplicable(order, marketPrice)) {
                logger.info("{} satisfies the trigger condition.", order.getSecurity());
                switch (order.getSide()) {
                    case BUY: executionService.buy(order.getSecurity(), marketPrice, order.getVolume());
                    break;
                    case SELL: executionService.sell(order.getSecurity(), order.getPrice(), order.getVolume());
                    break;
                    default:
                        throw new UnsupportedOperationException("Not supported");
                }
                orderCache.updateOrderStatus(order, OrderStatus.PLACED);
            }
        });
        logger.info("applyIfValid completed");
    }

    private boolean isApplicable(Order order, double marketPrice) {
        return marketPrice <= order.getPrice();
    }
}
