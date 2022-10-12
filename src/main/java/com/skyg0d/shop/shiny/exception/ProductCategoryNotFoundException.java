package com.skyg0d.shop.shiny.exception;

public class ProductCategoryNotFoundException extends RuntimeException {
    public ProductCategoryNotFoundException(String productSlug, String categorySlug) {
        super(String.format("[%s] category not found on product [%s]", categorySlug, productSlug));
    }
}
