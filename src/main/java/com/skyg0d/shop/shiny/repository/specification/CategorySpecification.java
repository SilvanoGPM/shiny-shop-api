package com.skyg0d.shop.shiny.repository.specification;

import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.search.CategoryParameterSearch;
import org.springframework.data.jpa.domain.Specification;

import static org.springframework.data.jpa.domain.Specification.where;

public class CategorySpecification extends AbstractSpecification {

    public static Specification<Category> getSpecification(CategoryParameterSearch search) {
        return where(withName(search.getName()))
                .and(where(withDescription(search.getDescription())))
                .and(where(withThumbnail(search.getThumbnail())))
                .and(where(withCreatedInDateOrAfter(search.getCreatedInDateOrAfter())))
                .and(where(withCreatedInDateOrBefore(search.getCreatedInDateOrBefore())));
    }

    private static Specification<Category> withName(String name) {
        return like(name, "name");
    }

    private static Specification<Category> withDescription(String description) {
        return like(description, "description");
    }

    private static Specification<Category> withThumbnail(String thumbnail) {
        return like(thumbnail, "thumbnail");
    }

}
