package com.skyg0d.shop.shiny.util;

import com.skyg0d.shop.shiny.model.EOrderStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StatusUtils {

    private final Map<EOrderStatus, String> statusMessages = new HashMap<>();

    public StatusUtils() {
        statusMessages.put(EOrderStatus.WAITING, "Order placed, awaiting payment confirmation");
        statusMessages.put(EOrderStatus.PAID, "Your order has been paid");
        statusMessages.put(EOrderStatus.SHIPPED, "Your order has been sent");
        statusMessages.put(EOrderStatus.ON_THE_WAY, "Your order is on the way");
        statusMessages.put(EOrderStatus.CANCELED, "Your order has been canceled");
        statusMessages.put(EOrderStatus.DELIVERED, "Your order has been delivered");
    }

    public String getStatusNotificationMessage(EOrderStatus status) {
        return statusMessages.get(status);
    }

}
