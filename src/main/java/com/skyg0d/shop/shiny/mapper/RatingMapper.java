package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.service.ProductService;
import com.skyg0d.shop.shiny.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class RatingMapper {

    @Autowired
    protected UserService userService;

    @Autowired
    protected ProductService productService;

    @Mappings({
            @Mapping(source = "userEmail", target = "user", qualifiedByName = "mapUserFromEmail"),
            @Mapping(source = "request.productSlug", target = "product", qualifiedByName = "mapProductFromSlug")
    })
    public abstract Rating toRating(CreateRatingRequest request, String userEmail);

    public abstract RatingResponse toRatingResponse(Rating rating);

    @Named("mapUserFromEmail")
    protected User mapUserFromEmail(String email) {
        return userService.findByEmail(email);
    }

    @Named("mapProductFromSlug")
    protected Product mapProductFromSlug(String slug) {
        return productService.findBySlug(slug);
    }

    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

}
