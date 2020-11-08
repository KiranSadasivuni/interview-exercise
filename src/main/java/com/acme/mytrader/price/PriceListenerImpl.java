package com.acme.mytrader.price;

import com.acme.mytrader.strategy.TradingStrategy;

public class PriceListenerImpl implements PriceListener {

    TradingStrategy tradingStrategy = new TradingStrategy();

    @Override
    public void priceUpdate(String security, double price) {
        tradingStrategy.applyIfValid(security, price);
    }
}