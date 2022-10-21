package com.skyg0d.shop.shiny.exception;

public class InactiveProductOnOrderException extends RuntimeException {
    public InactiveProductOnOrderException(String productSlug) {
        super(String.format("Product [%s] is inactive, could not add it to order.", productSlug));
    }
}
