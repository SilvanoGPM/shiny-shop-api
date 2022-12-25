package com.skyg0d.shop.shiny.payload;

import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationToAllRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNotificationParams {

    @NotBlank
    private String content;

    @NotBlank
    private String category;

    private User user;

    public static CreateNotificationParams fromRequest(CreateNotificationRequest request, User user) {
        return CreateNotificationParams
                .builder()
                .category(request.getCategory())
                .content(request.getContent())
                .user(user)
                .build();
    }

    public static CreateNotificationParams fromRequest(CreateNotificationToAllRequest request, User user) {
        return CreateNotificationParams
                .builder()
                .category(request.getCategory())
                .content(request.getContent())
                .user(user)
                .build();
    }

}
