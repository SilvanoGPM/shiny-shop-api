package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ProductCategoryNotFoundException;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.mapper.ProductMapper;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private final ProductMapper mapper = ProductMapper.INSTANCE;

    public Page<AdminProductResponse> listAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toAdminProductResponse);
    }

    public Page<UserProductResponse> listAllActive(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable).map(mapper::toUserProductResponse);
    }

    public Product findBySlug(String slug) throws ResourceNotFoundException {
        return productRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    public UserProductResponse findBySlugMapped(String slug) throws ResourceNotFoundException {
        return mapper.toUserProductResponse(findBySlug(slug));
    }

    public void verifySlugExists(String slug) throws SlugAlreadyExistsException {
        if (productRepository.existsBySlug(slug)) {
            throw new SlugAlreadyExistsException("Product", slug);
        }
    }

    public UserProductResponse create(CreateProductRequest request) {
        verifySlugExists(request.getSlug());

        Product productMapped = mapper.toProduct(request);
        productMapped.setCategories(getCategories(request.getCategories()));

        Product productSaved = productRepository.save(productMapped);

        return mapper.toUserProductResponse(productSaved);
    }

    public void replace(ReplaceProductRequest request) {
        Product productFound = findBySlug(request.getSlug());
        Product productMapped = mapper.toProduct(request);

        Set<Category> categories = getCategories(request.getCategories());

        productMapped.setId(productFound.getId());
        productMapped.setCategories(categories);

        productRepository.save(productMapped);
    }

    public void toggleActive(String slug) {
        Product productFound = findBySlug(slug);
        productFound.setActive(!productFound.isActive());

        productRepository.save(productFound);
    }

    public void applyDiscount(String slug, int discount) {
        Product productFound = findBySlug(slug);

        productFound.setDiscount(discount);

        productRepository.save(productFound);
    }

    public void changeAmount(String slug, long amount) {
        Product productFound = findBySlug(slug);

        productFound.setAmount(amount);

        productRepository.save(productFound);
    }

    public void addCategory(String productSlug, String categorySlug) {
        Product productFound = findBySlug(productSlug);
        Category categoryFound = categoryService.findBySlug(categorySlug);

        productFound.getCategories().add(categoryFound);

        productRepository.save(productFound);
    }

    public void removeCategory(String productSlug, String categorySlug) {
        Product productFound = findBySlug(productSlug);

        Predicate<Category> existsCategory = (category) -> category.getSlug().equalsIgnoreCase(categorySlug);

        boolean hasCategory = productFound.getCategories()
                .stream()
                .anyMatch(existsCategory);

        if (!hasCategory) {
            throw new ProductCategoryNotFoundException(productSlug, categorySlug);
        }

        productFound.getCategories().removeIf(existsCategory);

        productRepository.save(productFound);
    }

    public void delete(String slug) {
        productRepository.delete(findBySlug(slug));
    }

    private Set<Category> getCategories(Set<String> categories) {
        return categories
                .stream()
                .map(categoryService::findBySlug)
                .collect(Collectors.toSet());
    }

}
