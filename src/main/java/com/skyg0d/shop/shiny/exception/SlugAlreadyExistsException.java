package com.skyg0d.shop.shiny.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String prefix, String slug) {
        super(String.format("%s with slug [%s] already exists", prefix, slug));
    }
}
