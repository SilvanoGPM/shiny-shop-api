package com.skyg0d.shop.shiny.repository.specification;

import com.skyg0d.shop.shiny.converter.StringToEnumConverter;
import com.skyg0d.shop.shiny.model.EOrderStatus;
import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.payload.search.OrderParameterSearch;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.springframework.data.jpa.domain.Specification.where;

public class OrderSpecification extends AbstractSpecification {

    public static Specification<Order> getSpecification(OrderParameterSearch search) {
        return where(withGreaterThanOrEqualToPrice(search.getGreaterThanOrEqualToPrice()))
                .and(where(withLessThanOrEqualToPrice(search.getLessThanOrEqualToPrice())))
                .and(where(withCreatedInDateOrAfter(search.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(search.getCreatedInDateOrBefore())))
                .and(where(withProductName(search.getProductName())))
                .and(where(withProductDescription(search.getProductDescription())))
                .and(where(withProductBrand(search.getProductBrand())))
                .and(where(withUserUsername(search.getUserUsername())))
                .and(where(withUserFullName(search.getUserFullName())))
                .and(where(withUserEmail(search.getUserEmail())))
                .and(where(withStatus(search.getStatus())));
    }

    private static Specification<Order> withGreaterThanOrEqualToPrice(BigDecimal price) {
        return greaterThanOrEqual(price, "price");
    }

    private static Specification<Order> withLessThanOrEqualToPrice(BigDecimal price) {
        return lessThanOrEqual(price, "price");
    }

    private static Specification<Order> withProductName(String productName) {
        return likeProduct(productName, "name");
    }

    private static Specification<Order> withProductDescription(String productDescription) {
        return likeProduct(productDescription, "description");
    }

    private static Specification<Order> withProductBrand(String productBrand) {
        return likeProduct(productBrand, "brand");
    }

    private static Specification<Order> withUserUsername(String username) {
        return likeUser(username, "username");
    }

    private static Specification<Order> withUserFullName(String fullName) {
        return likeUser(fullName, "fullName");
    }

    private static Specification<Order> withUserEmail(String email) {
        return likeUser(email, "email");
    }

    private static Specification<Order> withStatus(String status) {
        EOrderStatus eStatus = (status == null || status.isBlank())
                ? null
                : new StringToEnumConverter().convert(status);

        return getSpec(status, (root, query, builder) -> (
                builder.equal(builder.lower(root.get("status")), eStatus)
        ));
    }

    private static Specification<Order> likeProduct(String string, String property) {
        return likeJoin("products", string, property);
    }

    private static Specification<Order> likeUser(String string, String property) {
        return likeJoin("user", string, property);
    }

}
