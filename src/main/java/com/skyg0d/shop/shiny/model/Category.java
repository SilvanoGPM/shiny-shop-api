package com.skyg0d.shop.shiny.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "slug")
        }
)
public class Category extends BaseEntity {

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    private String description;

    private String thumbnail;

}
