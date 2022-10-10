package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.mapper.CategoryMapper;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.request.CreateCategoryRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceCategoryRequest;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper = CategoryMapper.INSTANCE;

    public Page<CategoryResponse> listAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(mapper::toCategoryResponse);
    }

    public Category findBySlug(String slug) throws ResourceNotFoundException {
        return categoryRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    public CategoryResponse findBySlugMapped(String slug) throws ResourceNotFoundException {
        return mapper.toCategoryResponse(findBySlug(slug));
    }

    public void verifySlugExists(String slug) throws SlugAlreadyExistsException {
        if (categoryRepository.existsBySlug(slug)) {
            throw new SlugAlreadyExistsException("Category", slug);
        }
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        verifySlugExists(request.getSlug());

        Category categorySaved = categoryRepository.save(mapper.toCategory(request));

        return mapper.toCategoryResponse(categorySaved);
    }

    public void replace(ReplaceCategoryRequest request) {
        Category categoryFound = findBySlug(request.getSlug());
        Category categoryMapped = mapper.toCategory(request);

        categoryMapped.setId(categoryFound.getId());

        categoryRepository.save(categoryMapped);
    }

    public void delete(String slug) {
        categoryRepository.delete(findBySlug(slug));
    }

}
