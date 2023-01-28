package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.mapper.RatingMapper;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserService userService;

    private final ProductService productService;

    private final RatingMapper mapper;

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

    public RatingResponse create(CreateRatingRequest request, String userEmail) {
        Rating ratingToSave = mapper.toRating(request, userEmail);

        return mapper.toRatingResponse(ratingRepository.save(ratingToSave));
    }

}
