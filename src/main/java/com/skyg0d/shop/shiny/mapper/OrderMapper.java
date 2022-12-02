package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.payload.response.OrderProductResponse;
import com.skyg0d.shop.shiny.payload.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    public static final OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "paymentLink.paymentUrl", target = "paymentLink", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    public abstract OrderResponse toOrderResponse(Order order);

    List<OrderProductResponse> map(List<Product> products) {
        return products
                .stream()
                .collect(Collectors.groupingBy(Product::getSlug))
                .entrySet()
                .stream()
                .map((entry) -> {
                    String key = entry.getKey();
                    List<Product> productsValue = entry.getValue();
                    Product product = productsValue.get(0);

                    return OrderProductResponse
                            .builder()
                            .slug(key)
                            .name(product.getName())
                            .thumbnail(product.getThumbnail())
                            .brand(product.getBrand())
                            .price(product.getPrice())
                            .discount(product.getDiscount())
                            .amount((long) productsValue.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

}
