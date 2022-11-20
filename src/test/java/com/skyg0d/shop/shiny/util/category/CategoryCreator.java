package com.skyg0d.shop.shiny.util.category;

import com.skyg0d.shop.shiny.mapper.CategoryMapper;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.request.CreateCategoryRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceCategoryRequest;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.payload.search.CategoryParameterSearch;

public class CategoryCreator {

    public static final String SLUG = "test-category";

    public static final String NAME = "Test Category";

    public static final String DESCRIPTION = "Test Category Description";

    public static final String THUMBNAIL = "test-category-thumbnail";

    public static Category createCategoryToBeSave() {
        return Category
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .build();
    }

    public static Category createCategory() {
        return Category
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .build();
    }

    public static CategoryResponse createCategoryResponse() {
        return CategoryMapper.INSTANCE.toCategoryResponse(createCategory());
    }

    public static CreateCategoryRequest createCreateCategoryRequest() {
        return CreateCategoryRequest
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .build();
    }

    public static ReplaceCategoryRequest createReplaceCategoryRequest() {
        return ReplaceCategoryRequest
                .builder()
                .slug(SLUG)
                .name(NAME)
                .description(DESCRIPTION)
                .thumbnail(THUMBNAIL)
                .build();
    }

    public static CategoryParameterSearch createCategortyParameterSearch() {
        return CategoryParameterSearch
                .builder()
                .name(NAME)
                .build();
    }

}
