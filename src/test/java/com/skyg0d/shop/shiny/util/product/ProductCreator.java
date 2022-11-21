package com.skyg0d.shop.shiny.util.product;

import com.skyg0d.shop.shiny.mapper.ProductMapper;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .categories(new HashSet<>())
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
                .categories(new HashSet<>())
                .build();
    }

    public static AdminProductResponse createAdminProductResponse() {
        return ProductMapper.INSTANCE.toAdminProductResponse(createProduct());
    }

    public static UserProductResponse createUserProductResponse() {
        return ProductMapper.INSTANCE.toUserProductResponse(createProduct());
    }

    public static CreateProductRequest createCreateProductRequest() {
        return CreateProductRequest
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .brand(BRAND)
                .price(PRICE)
                .amount(AMOUNT)
                .images(new HashSet<>(IMAGES))
                .sizes(new HashSet<>(SIZES))
                .features(new HashSet<>(FEATURES))
                .categories(Set.of())
                .build();
    }

    public static ReplaceProductRequest createReplaceProductRequest() {
        return ReplaceProductRequest
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .brand(BRAND)
                .price(PRICE)
                .amount(AMOUNT)
                .images(new HashSet<>(IMAGES))
                .sizes(new HashSet<>(SIZES))
                .features(new HashSet<>(FEATURES))
                .categories(new HashSet<>())
                .build();
    }

    public static ProductParametersSearch createProductParametersSearch() {
        return ProductParametersSearch
                .builder()
                .name(NAME)
                .lessThanOrEqualToPrice(new BigDecimal(-1))
                .greaterThanOrEqualToPrice(new BigDecimal(-1))
                .build();
    }

}
