package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.annotations.IsUser;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.payload.response.RatingStarsAverageResponse;
import com.skyg0d.shop.shiny.service.RatingService;
import com.skyg0d.shop.shiny.util.AuthUtils;
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
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    private final AuthUtils authUtils;

    @GetMapping("/user/{email}/all")
    public ResponseEntity<Page<RatingResponse>> listAllByUser(@PathVariable String email, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ratingService.findAllByUser(email, pageable));
    }

    @GetMapping("/product/{slug}/all")
    public ResponseEntity<Page<RatingResponse>> listAllByProduct(@PathVariable String slug, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ratingService.findAllByProduct(slug, pageable));
    }

    @GetMapping("/product/{slug}/average")
    public ResponseEntity<RatingStarsAverageResponse> productStarsAverage(@PathVariable String slug) {
        return ResponseEntity.ok(ratingService.productStarsAverage(slug));
    }

    @PostMapping
    @IsUser
    public ResponseEntity<RatingResponse> create(@Valid @RequestBody CreateRatingRequest request) {
        return new ResponseEntity<>(ratingService.create(request, authUtils.getUserDetails().getEmail()), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ratingService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
