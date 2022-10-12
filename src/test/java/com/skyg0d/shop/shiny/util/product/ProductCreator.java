package com.skyg0d.shop.shiny.util.product;

import com.skyg0d.shop.shiny.model.Product;

import java.math.BigDecimal;
import java.util.List;

public class ProductCreator {

    public static final String SLUG = "test-product";
    public static final String NAME = "Test Product";
    public static final String DESCRIPTION = "Test Product Description";
    public static final String THUMBNAIL = "test-product-thumbnail";
    public static final String BRAND = "Test Product Brand";
    public static final BigDecimal PRICE = BigDecimal.valueOf(10);
    public static final long AMOUNT = 10;
    public static final List<String> IMAGES = List.of("test-product-image");
    public static final List<String> SIZES = List.of("Test Product Size");
    public static final List<String> FEATURES = List.of("Test Product Feature");

    public static Product createProductToBeSave() {
        return Product
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .brand(BRAND)
                .price(PRICE)
                .amount(AMOUNT)
                .images(IMAGES)
                .sizes(SIZES)
                .features(FEATURES)
                .build();
    }

    public static Product createProduct() {
        return Product
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .brand(BRAND)
                .price(PRICE)
                .amount(AMOUNT)
                .images(IMAGES)
                .sizes(SIZES)
                .features(FEATURES)
                .build();
    }

}
