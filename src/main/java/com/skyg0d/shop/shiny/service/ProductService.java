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
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import com.skyg0d.shop.shiny.property.StripeProps;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.repository.specification.ProductSpecification;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.PriceUpdateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductUpdateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private final StripeProps stripeProps;

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

    public Page<UserProductResponse> search(ProductParametersSearch search, Pageable pageable) {
        return productRepository
                .findAll(ProductSpecification.getSpecification(search), pageable)
                .map(mapper::toUserProductResponse);
    }

    public UserProductResponse create(CreateProductRequest request) throws StripeException {
        verifySlugExists(request.getSlug());

        Product productMapped = mapper.toProduct(request);
        productMapped.setCategories(getCategories(request.getCategories()));

        ProductCreateParams productParams = ProductCreateParams
                .builder()
                .setName(request.getName())
                .setDescription(request.getDescription())
                .putMetadata("slug", request.getSlug())
                .putMetadata("brand", request.getBrand())
                .addImage(request.getThumbnail())
                .addAllImage(new ArrayList<>(request.getImages()))
                .build();

        com.stripe.model.Product stripeProduct = com.stripe.model.Product
                .create(productParams);

        PriceCreateParams priceParams = PriceCreateParams
                .builder()
                .setProduct(stripeProduct.getId())
                .setUnitAmountDecimal(productMapped.getPrice())
                .setCurrency(stripeProps.getCurrency())
                .build();

        Price stripePrice = Price.create(priceParams);

        productMapped.setStripeProductId(stripeProduct.getId());
        productMapped.setStripePriceId(stripePrice.getId());

        Product productSaved = productRepository.save(productMapped);

        return mapper.toUserProductResponse(productSaved);
    }

    @Transactional
    public void replace(ReplaceProductRequest request) throws StripeException {
        Product productFound = findBySlug(request.getSlug());
        Product productMapped = mapper.toProduct(request);

        Set<Category> categories = getCategories(request.getCategories());

        productMapped.setId(productFound.getId());
        productMapped.setCategories(categories);
        productMapped.setStripePriceId(productFound.getStripePriceId());
        productMapped.setStripeProductId(productFound.getStripeProductId());

        Product productSaved = productRepository.save(productMapped);

        Price stripePrice = Price.retrieve(productSaved.getStripePriceId());

        com.stripe.model.Product stripeProduct =
                com.stripe.model.Product.retrieve(productSaved.getStripeProductId());

        ProductUpdateParams productUpdateParams = ProductUpdateParams
                .builder()
                .setName(productSaved.getName())
                .setDescription(productSaved.getDescription())
                .setImages(productSaved.getImages())
                .build();

        stripeProduct.update(productUpdateParams);
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

    @Transactional
    public void delete(String slug) throws StripeException {
        Product productFound = findBySlug(slug);

        Price.retrieve(productFound.getStripePriceId()).update(PriceUpdateParams.builder().setActive(false).build());

        com.stripe.model.Product.retrieve(productFound.getStripeProductId()).delete();

        productRepository.delete(productFound);
    }

    private Set<Category> getCategories(Set<String> categories) {
        return categories
                .stream()
                .map(categoryService::findBySlug)
                .collect(Collectors.toSet());
    }

}
