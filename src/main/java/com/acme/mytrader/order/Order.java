package com.acme.mytrader.order;

import java.util.Objects;

public class Order {
    private String security;
    private String placedBy;
    private double price;
    private int volume;
    private Side side;
    private OrderStatus orderStatus;

    public Order(String security, String placedBy, double price, int volume, Side side, OrderStatus orderStatus) {
        this.security = security;
        this.placedBy = placedBy;
        this.price = price;
        this.volume = volume;
        this.side = side;
        this.orderStatus = orderStatus;
    }


    public boolean isPreset() {
        return this.getOrderStatus().equals(OrderStatus.PRESET);
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getPlacedBy() {
        return placedBy;
    }

    public void setPlacedBy(String placedBy) {
        this.placedBy = placedBy;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (Double.compare(order.price, price) != 0) return false;
        if (volume != order.volume) return false;
        if (!Objects.equals(security, order.security)) return false;
        if (!Objects.equals(placedBy, order.placedBy)) return false;
        if (side != order.side) return false;
        return orderStatus == order.orderStatus;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = security != null ? security.hashCode() : 0;
        result = 31 * result + (placedBy != null ? placedBy.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + volume;
        result = 31 * result + (side != null ? side.hashCode() : 0);
        result = 31 * result + (orderStatus != null ? orderStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "security='" + security + '\'' +
                ", placedBy='" + placedBy + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", side=" + side +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
