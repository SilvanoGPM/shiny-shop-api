package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    public static final ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categories", ignore = true)
    public abstract Product toProduct(CreateProductRequest request);

    @Mapping(target = "categories", ignore = true)
    public abstract Product toProduct(ReplaceProductRequest request);

    public abstract UserProductResponse toUserProductResponse(Product product);

    public abstract AdminProductResponse toAdminProductResponse(Product product);

}
