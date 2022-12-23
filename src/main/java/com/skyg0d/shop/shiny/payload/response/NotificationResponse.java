package com.skyg0d.shop.shiny.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NotificationResponse {

    private String id;

    private String content;

    private String category;

    private UserResponse user;

    private LocalDateTime readAt;

    private LocalDateTime canceledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
