package com.acme.mytrader.order;

/**
 * The valid States of an Order.
 * NEW - Order is ready to be placed immediately
 * PRESET - Trader sets a price to be monitored.
 * PLACED - Order has been placed (for BUY or SELL)
 */
public enum OrderStatus {
    NEW,
    PRESET,
    PLACED
}
