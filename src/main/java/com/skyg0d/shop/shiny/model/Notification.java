package com.skyg0d.shop.shiny.model;

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
    private String content;

    @NotBlank
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime readAt;

    private LocalDateTime canceledAt;

}
