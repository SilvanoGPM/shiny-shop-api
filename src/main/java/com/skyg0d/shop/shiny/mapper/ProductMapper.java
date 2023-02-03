package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.service.CategoryService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductMapper {

    public static final ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Autowired
    private CategoryService categoryService;

    @Mapping(target = "categories", source = "categories", qualifiedByName = "mapCategories")
    public abstract Product toProduct(CreateProductRequest request);

    @Mapping(target = "categories", source = "request.categories", qualifiedByName = "mapCategories")

    public abstract Product toProduct(ReplaceProductRequest request, @MappingTarget Product product);

    public abstract UserProductResponse toUserProductResponse(Product product);

    public abstract AdminProductResponse toAdminProductResponse(Product product);

    @Named("mapCategories")
    protected Set<Category> mapCategories(Set<String> categories) {
        return categories
                .stream()
                .map(categoryService::findBySlug)
                .collect(Collectors.toSet());
    }

}
