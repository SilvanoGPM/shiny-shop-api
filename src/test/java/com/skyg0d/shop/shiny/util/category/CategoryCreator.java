package com.skyg0d.shop.shiny.util.category;

import com.skyg0d.shop.shiny.model.Category;

public class CategoryCreator {

    public static final String SLUG = "test-category";

    public static final String NAME = "Test Category";

    public static final String DESCRIPTION = "Test Description";

    public static final String THUMBNAIL = "Test Thumbnail";

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

}
