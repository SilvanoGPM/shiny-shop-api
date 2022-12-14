package com.skyg0d.shop.shiny.util.product;

import com.skyg0d.shop.shiny.mapper.ProductMapper;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.ApplyDiscountParams;
import com.skyg0d.shop.shiny.payload.PromotionCodeCreated;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
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

    public static final int DISCOUNT = 10;

    public static final String DISCOUNT_CODE = "TESTDISCOUNT";

    public static final String STRIPE_PRODUCT_ID = "test-stripe-product-id";

    public static final String STRIPE_PRICE_ID = "test-stripe-price-id";

    public static final String STRIPE_PROMOTION_CODE_ID = "test-stripe-promotion-code-id";

    public static final String STRIPE_COUPON_ID = "test-stripe-coupon-id";

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
                .stripeProductId(STRIPE_PRODUCT_ID)
                .stripePriceId(STRIPE_PRICE_ID)
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
                .stripeProductId(STRIPE_PRODUCT_ID)
                .stripePriceId(STRIPE_PRICE_ID)
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

    public static ApplyDiscountRequest createApplyDiscountRequest() {
        return ApplyDiscountRequest
                .builder()
                .name("test-discount")
                .discount(DISCOUNT)
                .code(DISCOUNT_CODE)
                .build();
    }

    public static ApplyDiscountParams createApplyDiscountParams() {
        return ApplyDiscountParams.fromRequest(createApplyDiscountRequest(), SLUG);
    }

    public static PromotionCodeCreated createPromotionCodeCreated() {
        return PromotionCodeCreated
                .builder()
                .promotionCodeId(STRIPE_PROMOTION_CODE_ID)
                .couponId(STRIPE_COUPON_ID)
                .build();
    }

}
