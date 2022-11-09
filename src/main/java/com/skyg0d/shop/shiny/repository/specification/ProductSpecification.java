package com.skyg0d.shop.shiny.repository.specification;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.springframework.data.jpa.domain.Specification.where;

public class ProductSpecification extends AbstractSpecification {

    public static Specification<Product> getSpecification(ProductParametersSearch search) {
        return where(withName(search.getName()))
                .and(where(withDescription(search.getDescription())))
                .and(where(withThumbnail(search.getThumbnail())))
                .and(where(withBrand(search.getBrand())))
                .and(where(withGreaterThanOrEqualToPrice(search.getGreaterThanOrEqualToPrice())))
                .and(where(withLessThanOrEqualToPrice(search.getLessThanOrEqualToPrice())))
                .and(where(withGreaterThanOrEqualToAmount(search.getGreaterThenOrEqualToAmount())))
                .and(where(withLessThanOrEqualToAmount(search.getLessThenOrEqualToAmount())))
                .and(where(withGreaterThanOrEqualToDiscount(search.getGreaterThenOrEqualToDiscount())))
                .and(where(withLessThanOrEqualToDiscount(search.getLessThenOrEqualToDiscount())))
                .and(where(withActive(search.getActive())))
                .and(where(withCreatedInDateOrAfter(search.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(search.getCreatedInDateOrBefore())));
    }

    private static Specification<Product> withName(String name) {
        return like(name, "name");
    }

    private static Specification<Product> withDescription(String description) {
        return like(description, "description");
    }

    private static Specification<Product> withThumbnail(String thumbnail) {
        return like(thumbnail, "thumbnail");
    }

    private static Specification<Product> withBrand(String brand) {
        return like(brand, "brand");
    }

    private static Specification<Product> withGreaterThanOrEqualToPrice(BigDecimal price) {
        return greaterThanOrEqual(price, "price");
    }

    private static Specification<Product> withLessThanOrEqualToPrice(BigDecimal price) {
        return lessThanOrEqual(price, "price");
    }

    private static Specification<Product> withGreaterThanOrEqualToAmount(long amount) {
        return greaterThanOrEqual(amount, "amount");
    }

    private static Specification<Product> withLessThanOrEqualToAmount(long amount) {
        return lessThanOrEqual(amount, "amount");
    }

    private static Specification<Product> withGreaterThanOrEqualToDiscount(int discount) {
        return greaterThanOrEqual(discount, "amount");
    }

    private static Specification<Product> withLessThanOrEqualToDiscount(int discount) {
        return lessThanOrEqual(discount, "discount");
    }

    private static Specification<Product> withActive(int active) {
        return withBoolean(active, "active");
    }

}
