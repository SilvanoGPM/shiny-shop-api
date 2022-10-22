package com.skyg0d.shop.shiny.payload.response;

import com.skyg0d.shop.shiny.model.EOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse {

    private UUID id;

    private String createdAt;

    private BigDecimal price;

    private List<OrderProductResponse> products;

    private UserResponse user;

    private EOrderStatus status;

}