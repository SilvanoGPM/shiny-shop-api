package com.skyg0d.shop.shiny.util.order;

import com.skyg0d.shop.shiny.mapper.OrderMapper;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateOrderProduct;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

import static com.skyg0d.shop.shiny.util.product.ProductCreator.createProduct;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createUser;

public class OrderCreator {

    public static final String EMAIL = "test@mail.com";

    public static Order createOrder() {
        return Order
                .builder()
                .price(new BigDecimal(10))
                .products(List.of(createProduct()))
                .user(createUser())
                .status(EOrderStatus.SHIPPING)
                .build();
    }

    public static OrderResponse createOrderResponse() {
        return OrderMapper.INSTANCE.toOrderResponse(createOrder());
    }

    public static CreateOrderRequest createCreateOrderRequest() {
        return CreateOrderRequest
                .builder()
                .products(List.of(new CreateOrderProduct("test-slug", 10)))
                .userEmail(EMAIL)
                .build();
    }

}
