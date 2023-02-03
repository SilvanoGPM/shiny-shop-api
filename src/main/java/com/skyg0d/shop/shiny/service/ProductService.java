package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ProductCategoryNotFoundException;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.mapper.ProductMapper;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.ApplyDiscountParams;
import com.skyg0d.shop.shiny.payload.PromotionCodeCreated;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.repository.specification.ProductSpecification;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.ProductUpdateParams;
import com.stripe.param.common.EmptyParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private final StripeService stripeService;

    private final ProductMapper mapper;

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

    public AdminProductResponse create(CreateProductRequest request) throws StripeException {
        verifySlugExists(request.getSlug());

        Product productMapped = mapper.toProduct(request);
        com.stripe.model.Product stripeProduct = stripeService.createProduct(request);

        Price stripePrice = stripeService.createPrice(stripeProduct.getId(), productMapped.getPrice());

        productMapped.setStripeProductId(stripeProduct.getId());
        productMapped.setStripePriceId(stripePrice.getId());

        Product productSaved = productRepository.save(productMapped);

        return mapper.toAdminProductResponse(productSaved);
    }

    @Transactional
    public void replace(ReplaceProductRequest request) throws StripeException {
        Product productFound = findBySlug(request.getSlug());
        Product productMapped = mapper.toProduct(request, productFound);

        boolean priceHasChanged = productFound.getPrice().compareTo(request.getPrice()) != 0;

        if (priceHasChanged) {
            stripeService.desactivePrice(productFound.getStripePriceId());
            stripeService.createPrice(productFound.getStripeProductId(), request.getPrice());
        }

        Product productSaved = productRepository.save(productMapped);

        ProductUpdateParams productUpdateParams = ProductUpdateParams
                .builder()
                .setName(productSaved.getName())
                .setDescription(productSaved.getDescription())
                .setImages(productSaved.getImages())
                .build();

        stripeService.updateProduct(productSaved.getStripeProductId(), productUpdateParams);
    }

    public void toggleActive(String slug) throws StripeException {
        Product productFound = findBySlug(slug);

        boolean isActive = !productFound.isActive();
        productFound.setActive(isActive);
        stripeService.setProductActive(productFound.getStripeProductId(), isActive);

        productRepository.save(productFound);
    }

    @Transactional
    public void applyDiscount(ApplyDiscountParams params) throws StripeException {
        Product productFound = findBySlug(params.getProductSlug());

        PromotionCodeCreated promotionCode = stripeService.createPromotionCode(params, productFound.getStripeProductId());

        productFound.setDiscount(params.getDiscount());
        productFound.setDiscountCode(params.getCode());
        productFound.setStripePromotionCodeId(promotionCode.getPromotionCodeId());
        productFound.setStripeCouponId(promotionCode.getCouponId());

        productRepository.save(productFound);
    }

    @Transactional
    public void removeDiscount(String productSlug) throws StripeException {
        Product productFound = findBySlug(productSlug);

        stripeService.deletePromotionCode(productFound.getStripePromotionCodeId(), productFound.getStripeCouponId());

        productFound.setStripeCouponId(null);
        productFound.setStripePromotionCodeId(null);
        productFound.setDiscountCode(null);
        productFound.setDiscount(0);

        productRepository.save(productFound);
    }

    public void changeAmount(String slug, long amount) {
        Product productFound = findBySlug(slug);

        productFound.setAmount(amount);

        productRepository.save(productFound);
    }

    public void addCategory(String productSlug, String categorySlug) throws StripeException {
        Product productFound = findBySlug(productSlug);
        Category categoryFound = categoryService.findBySlug(categorySlug);

        productFound.getCategories().add(categoryFound);

        stripeService.updateProductMetadata(productFound.getStripeProductId(), productFound.getCategories());

        productRepository.save(productFound);
    }

    public void removeCategory(String productSlug, String categorySlug) throws StripeException {
        Product productFound = findBySlug(productSlug);

        Predicate<Category> existsCategory = (category) -> category.getSlug().equalsIgnoreCase(categorySlug);

        boolean hasCategory = productFound.getCategories()
                .stream()
                .anyMatch(existsCategory);

        if (!hasCategory) {
            throw new ProductCategoryNotFoundException(productSlug, categorySlug);
        }

        productFound.getCategories().removeIf(existsCategory);

        stripeService.updateProductMetadata(productFound.getStripeProductId(), productFound.getCategories());

        productRepository.save(productFound);
    }

    @Transactional
    public void delete(String slug) throws StripeException {
        Product productFound = findBySlug(slug);

        try {
            stripeService.desactivePrice(productFound.getStripePriceId());
            stripeService.updateProduct(productFound.getStripeProductId(), ProductUpdateParams.builder().setDefaultPrice(EmptyParam.EMPTY).build());
            stripeService.deleteProduct(productFound.getStripeProductId());
        } catch (Exception ex) {
            stripeService.setProductActive(productFound.getStripeProductId(), false);
        }

        productRepository.delete(productFound);
    }

}
