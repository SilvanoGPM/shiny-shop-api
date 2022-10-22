package com.skyg0d.shop.shiny.exception;

public class OrderPermissionInsufficient extends RuntimeException {
    public OrderPermissionInsufficient() {
        super("Permission insufficient to update order");
    }
}
