package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.PermissionInsufficient;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.mapper.RatingMapper;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.payload.response.RatingStarsAverageResponse;
import com.skyg0d.shop.shiny.repository.RatingRepository;
import com.skyg0d.shop.shiny.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserService userService;

    private final ProductService productService;

    private final RatingMapper mapper;

    private final AuthUtils authUtils;

    public Page<RatingResponse> findAllByUser(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);

        return ratingRepository
                .findAllByUser(user, pageable)
                .map(mapper::toRatingResponse);
    }

    public Page<RatingResponse> findAllByProduct(String productSlug, Pageable pageable) {
        Product product = productService.findBySlug(productSlug);

        return ratingRepository
                .findAllByProduct(product, pageable)
                .map(mapper::toRatingResponse);
    }

    public RatingStarsAverageResponse productStarsAverage(String productSlug) {
        productService.findBySlug(productSlug);

        return RatingStarsAverageResponse
                .builder()
                .stars(ratingRepository.productStarsAverage(productSlug))
                .build();
    }

    public RatingResponse create(CreateRatingRequest request, String userEmail) {
        Rating ratingToSave = mapper.toRating(request, userEmail);

        return mapper.toRatingResponse(ratingRepository.save(ratingToSave));
    }

    public void delete(String id) {
        Rating rating = findById(id);

        validateUser(rating);

        ratingRepository.delete(rating);
    }

    private Rating findById(String id) throws ResourceNotFoundException {
        return ratingRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
    }

    private void validateUser(Rating rating) {
        boolean isOwnerOrStaff = authUtils.isOwnerOrStaff(rating.getUser().getEmail());

        if (!isOwnerOrStaff) {
            throw new PermissionInsufficient("rating");
        }
    }

}
