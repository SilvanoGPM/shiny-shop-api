package com.skyg0d.shop.shiny.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @NotBlank
    @Schema(description = "Content of notification")
    private String content;

    @NotBlank
    @Schema(description = "Type of notification")
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "Notification recipient")
    private User user;

    @Schema(description = "Notification read time (null when not read)")
    private LocalDateTime readAt;

    @Schema(description = "Notification canceled time (null when not canceled)")
    private LocalDateTime canceledAt;

}
