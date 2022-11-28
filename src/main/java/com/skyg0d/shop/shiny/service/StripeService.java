package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.property.StripeProps;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceUpdateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductUpdateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StripeService {

    private final StripeProps stripeProps;

    @Autowired
    public StripeService(StripeProps stripeProps) {
        this.stripeProps = stripeProps;
//        this.userService = userService;

        Stripe.apiKey = stripeProps.getSecretKey();
    }

    public Product retrieveProduct(String productId) throws StripeException {
        return Product.retrieve(productId);
    }

    public Product createProduct(CreateProductRequest request) throws StripeException {
        ProductCreateParams productParams = ProductCreateParams
                .builder()
                .setName(request.getName())
                .setDescription(request.getDescription())
                .putMetadata("slug", request.getSlug())
                .putMetadata("brand", request.getBrand())
                .putMetadata("categories", String.join(", ", request.getCategories()))
                .addImage(request.getThumbnail())
                .addAllImage(new ArrayList<>(request.getImages()))
                .build();

        return Product.create(productParams);
    }

    public Price createPrice(String productId, BigDecimal price) throws StripeException {
        return Price.create(PriceCreateParams
                .builder()
                .setProduct(productId)
                .setUnitAmountDecimal(new BigDecimal(100).multiply(price))
                .setCurrency(stripeProps.getCurrency())
                .build());
    }

    public void updateProduct(String productId, ProductUpdateParams params) throws StripeException {
        retrieveProduct(productId).update(params);
    }

    public void updateProductMetadata(String productId, Set<Category> categories) throws StripeException {
        String categoriesString = categories
                .stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));

        ProductUpdateParams productParams = ProductUpdateParams
                .builder()
                .putMetadata("categories", categoriesString)
                .build();

        updateProduct(productId, productParams);
    }

    public void setProductActive(String productId, boolean active) throws StripeException {
        updateProduct(productId, ProductUpdateParams.builder().setActive(active).build());
    }

    public void desactivePrice(String priceId) throws StripeException {
        Price.retrieve(priceId).update(PriceUpdateParams.builder().setActive(false).build());
    }

    public void deleteProduct(String productId) throws StripeException {
        retrieveProduct(productId).delete();
    }

}
