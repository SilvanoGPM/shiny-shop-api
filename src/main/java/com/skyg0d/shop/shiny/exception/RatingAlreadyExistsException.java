package com.skyg0d.shop.shiny.exception;

public class RatingAlreadyExistsException extends RuntimeException {
    public RatingAlreadyExistsException() {
        super("Rating already exists for this product");
    }
}
