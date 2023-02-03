package com.skyg0d.shop.shiny.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "slug")
        }
)
public class Product extends BaseEntity {

    @NotBlank
    @Schema(description = "Slug of product")
    private String slug;

    @NotBlank
    @Schema(description = "Name of product")
    private String name;

    @Schema(description = "Description of product")
    private String description;

    @Schema(description = "Thumbnail image of product")
    private String thumbnail;

    @Schema(description = "Brand of product")
    private String brand;

    @Schema(description = "Id of product in stripe")
    private String stripeProductId;

    @Schema(description = "Id of price in stripe")
    private String stripePriceId;

    @Schema(description = "Id of promotion code in stripe")
    private String stripePromotionCodeId;

    @Schema(description = "Id of coupon in stripe")
    private String stripeCouponId;

    @Positive
    @Schema(description = "Base price of product")
    private BigDecimal price;

    @PositiveOrZero
    @Schema(description = "Quantity of products in stock")
    private long amount;

    @PositiveOrZero
    @Max(100)
    @Builder.Default
    @Schema(description = "Product discount percentage")
    private int discount = 0;

    @Schema(description = "Code to apply discount")
    private String discountCode;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Schema(description = "Product categories")
    private Set<Category> categories = new HashSet<>();

    @Builder.Default
    @ElementCollection(targetClass = String.class)
    @Schema(description = "Product images")
    private List<String> images = new ArrayList<>();

    @Builder.Default
    @ElementCollection(targetClass = String.class)
    @Schema(description = "Product sizes (empty if the product has no sizes to choose from)")
    private List<String> sizes = new ArrayList<>();

    @Builder.Default
    @ElementCollection(targetClass = String.class)
    @Schema(description = "Product features (empty if the product has no features to show)")
    private List<String> features = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Flag to check if a product is active")
    private boolean active = true;

}
