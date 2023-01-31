package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.annotations.IsStaff;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.payload.ApplyDiscountParams;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import com.skyg0d.shop.shiny.service.ProductService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Returns all active products with pagination", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<UserProductResponse>> listAllActive(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.listAllActive(pageable));
    }

    @GetMapping("/all")
    @IsStaff
    @Operation(summary = "Returns all products with pagination", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<AdminProductResponse>> listAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.listAll(pageable));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Returns product by slug", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "When product not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserProductResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.findBySlugMapped(slug));
    }

    @RequestMapping(value = "/{slug}", method = RequestMethod.HEAD)
    @Operation(summary = "Verify if product exists by slug", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product exists, not available"),
            @ApiResponse(responseCode = "404", description = "Product not exists, available"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> existsBySlug(@PathVariable String slug) {
        try {
            productService.verifySlugExists(slug);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SlugAlreadyExistsException ex) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Returns products searched with pagination", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Page<UserProductResponse>> search(@ParameterObject ProductParametersSearch search, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.search(search, pageable));
    }

    @PostMapping
    @IsAdmin
    @Operation(summary = "Persists a new product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<AdminProductResponse> create(@Valid @RequestBody CreateProductRequest request) throws StripeException {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    @PutMapping
    @IsStaff
    @Operation(summary = "Updates product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> replace(@Valid @RequestBody ReplaceProductRequest request) throws StripeException {
        productService.replace(request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{slug}/toggle/active")
    @IsStaff
    @Operation(summary = "Toggle product active", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> toggleActive(@PathVariable String slug) throws StripeException {
        productService.toggleActive(slug);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{slug}/apply/discount")
    @IsAdmin
    @Operation(summary = "Apply discount to product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> applyDiscount(@PathVariable String slug, @Valid @RequestBody ApplyDiscountRequest request) throws StripeException {
        productService.applyDiscount(ApplyDiscountParams.fromRequest(request, slug));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{slug}/remove/discount")
    @IsAdmin
    @Operation(summary = "Remove discount of product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> removeDiscount(@PathVariable String slug) throws StripeException {
        productService.removeDiscount(slug);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{slug}/change/amount")
    @IsStaff
    @Operation(summary = "Change product amount", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> changeAmount(@PathVariable String slug, @Valid @RequestBody ChangeAmountRequest request) {
        productService.changeAmount(slug, request.getAmount());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{productSlug}/add/{categorySlug}/category")
    @IsStaff
    @Operation(summary = "Add category to product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> addCategory(@PathVariable String productSlug, @PathVariable String categorySlug) throws StripeException {
        productService.addCategory(productSlug, categorySlug);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{productSlug}/remove/{categorySlug}/category")
    @IsStaff
    @Operation(summary = "Removes category of product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> removeCategory(@PathVariable String productSlug, @PathVariable String categorySlug) throws StripeException {
        productService.removeCategory(productSlug, categorySlug);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{slug}")
    @IsAdmin
    @Operation(summary = "Removes product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<Void> delete(@PathVariable String slug) throws StripeException {
        productService.delete(slug);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
