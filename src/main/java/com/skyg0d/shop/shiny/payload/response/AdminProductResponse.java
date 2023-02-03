package com.skyg0d.shop.shiny.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProductResponse {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String slug;

    private String name;

    private String description;

    private String thumbnail;

    private String brand;

    private BigDecimal price;

    private long amount;

    private int discount;

    private String discountCode;

    private String stripeProductId;

    private String stripePriceId;

    private String stripePromotionCodeId;

    private String stripeCouponId;

    @Builder.Default
    private Set<CategoryResponse> categories = new HashSet<>();

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Builder.Default
    private List<String> sizes = new ArrayList<>();

    @Builder.Default
    private List<String> features = new ArrayList<>();

    private boolean active;

}
