package com.skyg0d.shop.shiny.exception;

public class ProductOverflowAmountException extends RuntimeException {
    public ProductOverflowAmountException(String productSlug, long amount) {
        super(String.format("The product [%s] has only [%d] units", productSlug, amount));
    }
}
