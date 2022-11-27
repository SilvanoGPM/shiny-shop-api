package com.skyg0d.shop.shiny.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.*;

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
    private String slug;

    @NotBlank
    private String name;

    private String description;

    private String thumbnail;

    private String brand;

    private String stripeProductId;

    private String stripePriceId;

    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private long amount;

    @PositiveOrZero
    @Max(100)
    @Builder.Default
    private int discount = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    private List<String> images = new ArrayList<>();

    @ElementCollection(targetClass = String.class)
    private List<String> sizes = new ArrayList<>();

    @ElementCollection(targetClass = String.class)
    private List<String> features = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

}
