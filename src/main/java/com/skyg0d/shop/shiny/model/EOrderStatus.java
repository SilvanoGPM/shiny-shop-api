package com.skyg0d.shop.shiny.model;

public enum EOrderStatus {
    WAITING("Order placed, awaiting payment confirmation"),

    PAID("Your order has been paid"),

    SHIPPED("Your order has been sent"),

    ON_THE_WAY("Your order is on the way"),

    CANCELED("Your order has been canceled"),

    DELIVERED("Your order has been delivered");

    private final String notificationMessage;

    EOrderStatus(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }
}
