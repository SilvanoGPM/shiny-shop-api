package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.mapper.ProductMapper;
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

import java.lang.module.ResolutionException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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

    public void verifySlugExists(String slug) throws SlugAlreadyExistsException {
        if (productRepository.existsBySlug(slug)) {
            throw new SlugAlreadyExistsException(slug);
        }
    }

    public UserProductResponse create(CreateProductRequest request) {
        verifySlugExists(request.getSlug());

        Product productSaved = productRepository.save(mapper.toProduct(request));

        return mapper.toUserProductResponse(productSaved);
    }

    public void replace(ReplaceProductRequest request) {
        Product productFound = findBySlug(request.getSlug());
        Product productMapped = mapper.toProduct(request);

        productMapped.setId(productFound.getId());

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

    public void delete(String slug) {
        productRepository.delete(findBySlug(slug));
    }

}
