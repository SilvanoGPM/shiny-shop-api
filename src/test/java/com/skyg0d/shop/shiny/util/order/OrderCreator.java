package com.skyg0d.shop.shiny.util.order;

import com.skyg0d.shop.shiny.mapper.OrderMapper;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.MyPaymentLink;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.payload.request.CreateOrderProduct;
import com.skyg0d.shop.shiny.payload.request.CreateOrderRequest;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import com.skyg0d.shop.shiny.payload.search.OrderParameterSearch;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.product.ProductCreator.createProduct;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createUser;

public class OrderCreator {

    public static final UUID ID = UUID.fromString("d772f291-922d-4a5d-a2eb-f790856a5422");
    public static final String STRIPE_PAYMENT_ID = "test-payment-id";
    public static final String STRIPE_PAYMENT_URL = "test-payment-url";

    public static Order createOrder() {
        return Order
                .builder()
                .id(ID)
                .price(new BigDecimal(10))
                .products(List.of(createProduct()))
                .user(createUser())
                .status(EOrderStatus.WAITING)
                .paymentLink(MyPaymentLink
                        .builder()
                        .paymentId(STRIPE_PAYMENT_ID)
                        .paymentUrl(STRIPE_PAYMENT_URL)
                        .build())
                .build();
    }

    public static OrderResponse createOrderResponse() {
        return OrderMapper.INSTANCE.toOrderResponse(createOrder());
    }

    public static CreateOrderRequest createCreateOrderRequest() {
        return CreateOrderRequest
                .builder()
                .products(List.of(new CreateOrderProduct("test-slug", 10, "")))
                .build();
    }

    public static OrderParameterSearch createOrderParameterSearch() {
        return OrderParameterSearch
                .builder()
                .greaterThanOrEqualToPrice(new BigDecimal(-1))
                .lessThanOrEqualToPrice(new BigDecimal(-1))
                .status(EOrderStatus.WAITING.name())
                .build();
    }

}
