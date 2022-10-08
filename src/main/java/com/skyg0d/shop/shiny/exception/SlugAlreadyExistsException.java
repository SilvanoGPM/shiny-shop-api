package com.skyg0d.shop.shiny.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String slug) {
        super(String.format("Product with slug [%s] already exists", slug));
    }
}
