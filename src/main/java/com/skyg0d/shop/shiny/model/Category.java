package com.skyg0d.shop.shiny.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

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
    @Schema(description = "Slug of category")
    private String slug;

    @NotBlank
    @Schema(description = "Name of category")
    private String name;

    @Schema(description = "Description of category")
    private String description;

    @Schema(description = "Thumbnail image of category")
    private String thumbnail;

}
