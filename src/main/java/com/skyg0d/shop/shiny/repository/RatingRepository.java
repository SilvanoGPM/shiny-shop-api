package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Page<Rating> findAllByUser(User user, Pageable pageable);

    Page<Rating> findAllByProduct(Product product, Pageable pageable);


}
