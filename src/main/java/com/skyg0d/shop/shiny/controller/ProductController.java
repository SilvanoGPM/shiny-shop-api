package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.annotations.IsStaff;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import com.skyg0d.shop.shiny.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<UserProductResponse>> listAllActive(Pageable pageable) {
        return ResponseEntity.ok(productService.listAllActive(pageable));
    }

    @GetMapping("/all")
    @IsStaff
    public ResponseEntity<Page<AdminProductResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(productService.listAll(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<UserProductResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.findBySlugMapped(slug));
    }

    @GetMapping("/{slug}/exists")
    public ResponseEntity<MessageResponse> existsBySlug(@PathVariable String slug) {
        String message = "Product don't exists";

        try {
            productService.verifySlugExists(slug);
        } catch (SlugAlreadyExistsException ex) {
            message = "Product exists";
        }

        return ResponseEntity.ok(new MessageResponse(message));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> search(@ParameterObject ProductParametersSearch search, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.search(search, pageable));
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<UserProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    @PutMapping
    @IsStaff
    public ResponseEntity<MessageResponse> replace(@Valid @RequestBody ReplaceProductRequest request) {
        productService.replace(request);

        return ResponseEntity.ok(new MessageResponse("Product replaced!"));
    }

    @PatchMapping("/{slug}/toggle/active")
    @IsStaff
    public ResponseEntity<MessageResponse> toggleActive(@PathVariable String slug) {
        productService.toggleActive(slug);

        return ResponseEntity.ok(new MessageResponse("Product visibility toggle!"));
    }

    @PatchMapping("/{slug}/apply/discount")
    @IsStaff
    public ResponseEntity<MessageResponse> applyDiscount(@PathVariable String slug, @Valid @RequestBody ApplyDiscountRequest request) {
        productService.applyDiscount(slug, request.getDiscount());

        return ResponseEntity.ok(new MessageResponse("Product discount applied!"));
    }

    @PatchMapping("/{slug}/change/amount")
    @IsStaff
    public ResponseEntity<MessageResponse> changeAmount(@PathVariable String slug, @Valid @RequestBody ChangeAmountRequest request) {
        productService.changeAmount(slug, request.getAmount());

        return ResponseEntity.ok(new MessageResponse("Product amount changed!"));
    }

    @PatchMapping("/{productSlug}/add/{categorySlug}/category")
    @IsStaff
    public ResponseEntity<MessageResponse> addCategory(@PathVariable String productSlug, @PathVariable String categorySlug) {
        productService.addCategory(productSlug, categorySlug);

        return ResponseEntity.ok(new MessageResponse("Add category to product!"));
    }

    @PatchMapping("/{productSlug}/remove/{categorySlug}/category")
    @IsStaff
    public ResponseEntity<MessageResponse> removeCategory(@PathVariable String productSlug, @PathVariable String categorySlug) {
        productService.removeCategory(productSlug, categorySlug);

        return ResponseEntity.ok(new MessageResponse("Remove product category!"));
    }

    @DeleteMapping("/{slug}")
    @IsAdmin
    public ResponseEntity<MessageResponse> delete(@PathVariable String slug) {
        productService.delete(slug);

        return ResponseEntity.ok(new MessageResponse("Product removed!"));
    }

}
