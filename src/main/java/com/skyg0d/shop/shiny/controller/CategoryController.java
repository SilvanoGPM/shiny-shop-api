package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.payload.request.CreateCategoryRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceCategoryRequest;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> listAll(Pageable pageable) {
        return ResponseEntity.ok(categoryService.listAll(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.findBySlugMapped(slug));
    }

    @GetMapping("/{slug}/exists")
    public ResponseEntity<MessageResponse> existsBySlug(@PathVariable String slug) {
        String message = "Category don't exists";

        try {
            categoryService.verifySlugExists(slug);
        } catch (SlugAlreadyExistsException ex) {
            message = "Category exists";
        }

        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return new ResponseEntity<>(categoryService.create(request), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> replace(@Valid @RequestBody ReplaceCategoryRequest request) {
        categoryService.replace(request);

        return ResponseEntity.ok(new MessageResponse("Category replaced!"));
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(@PathVariable String slug) {
        categoryService.delete(slug);

        return ResponseEntity.ok(new MessageResponse("Category removed!"));
    }

}
