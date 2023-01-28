package com.skyg0d.shop.shiny.exception;

public class PermissionInsufficient extends RuntimeException {
    public PermissionInsufficient(String entityName) {
        super("Permission insufficient to modify " + entityName);
    }
}
