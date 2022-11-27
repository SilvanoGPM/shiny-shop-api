package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsAdmin;
import com.skyg0d.shop.shiny.annotations.IsStaff;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
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
            @ApiResponse(responseCode = "400", description = "When category not found"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<UserProductResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.findBySlugMapped(slug));
    }

    @GetMapping("/{slug}/exists")
    @Operation(summary = "Verify if product exists by slug", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
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
    public ResponseEntity<UserProductResponse> create(@Valid @RequestBody CreateProductRequest request) throws StripeException {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    @PutMapping
    @IsStaff
    @Operation(summary = "Updates product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> replace(@Valid @RequestBody ReplaceProductRequest request) throws StripeException {
        productService.replace(request);

        return ResponseEntity.ok(new MessageResponse("Product replaced!"));
    }

    @PatchMapping("/{slug}/toggle/active")
    @IsStaff
    @Operation(summary = "Toggle product active", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> toggleActive(@PathVariable String slug) throws StripeException {
        productService.toggleActive(slug);

        return ResponseEntity.ok(new MessageResponse("Product visibility toggle!"));
    }

    @PatchMapping("/{slug}/apply/discount")
    @IsStaff
    @Operation(summary = "Apply discount to product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> applyDiscount(@PathVariable String slug, @Valid @RequestBody ApplyDiscountRequest request) {
        productService.applyDiscount(slug, request.getDiscount());

        return ResponseEntity.ok(new MessageResponse("Product discount applied!"));
    }

    @PatchMapping("/{slug}/change/amount")
    @IsStaff
    @Operation(summary = "Change product amount", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> changeAmount(@PathVariable String slug, @Valid @RequestBody ChangeAmountRequest request) {
        productService.changeAmount(slug, request.getAmount());

        return ResponseEntity.ok(new MessageResponse("Product amount changed!"));
    }

    @PatchMapping("/{productSlug}/add/{categorySlug}/category")
    @IsStaff
    @Operation(summary = "Add category to product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> addCategory(@PathVariable String productSlug, @PathVariable String categorySlug) throws StripeException {
        productService.addCategory(productSlug, categorySlug);

        return ResponseEntity.ok(new MessageResponse("Add category to product!"));
    }

    @PatchMapping("/{productSlug}/remove/{categorySlug}/category")
    @IsStaff
    @Operation(summary = "Removes category of product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> removeCategory(@PathVariable String productSlug, @PathVariable String categorySlug) throws StripeException {
        productService.removeCategory(productSlug, categorySlug);

        return ResponseEntity.ok(new MessageResponse("Remove product category!"));
    }

    @DeleteMapping("/{slug}")
    @IsAdmin
    @Operation(summary = "Removes product", tags = "Products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "When not authorized"),
            @ApiResponse(responseCode = "403", description = "When forbidden"),
            @ApiResponse(responseCode = "500", description = "When server error")
    })
    public ResponseEntity<MessageResponse> delete(@PathVariable String slug) throws StripeException {
        productService.delete(slug);

        return ResponseEntity.ok(new MessageResponse("Product removed!"));
    }

}
