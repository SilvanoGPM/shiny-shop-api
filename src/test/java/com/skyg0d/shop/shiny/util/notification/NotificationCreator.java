package com.skyg0d.shop.shiny.util.notification;

import com.skyg0d.shop.shiny.mapper.NotificationMapper;
import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationToAllRequest;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.payload.search.NotificationParameterSearch;

import static com.skyg0d.shop.shiny.util.user.UserCreator.createUser;

public class NotificationCreator {

    public static final String CATEGORY = "test";
    public static final String CONTENT = "Test";

    public static Notification createNotification() {
        return Notification
                .builder()
                .user(createUser())
                .category(CATEGORY)
                .content(CONTENT)
                .build();
    }

    public static NotificationResponse createNotificationResponse() {
        return NotificationMapper.INSTANCE.toNotificationResponse(createNotification());
    }

    public static NotificationParameterSearch createNotificationParameterSearch() {
        return NotificationParameterSearch
                .builder()
                .content(CONTENT)
                .build();
    }

    public static CreateNotificationRequest createCreateNotificationRequest() {
        return CreateNotificationRequest
                .builder()
                .userEmail(createUser().getEmail())
                .category(CATEGORY)
                .content(CONTENT)
                .build();
    }

    public static CreateNotificationToAllRequest createCreateNotificationToAllRequest() {
        return CreateNotificationToAllRequest
                .builder()
                .category(CATEGORY)
                .content(CONTENT)
                .build();
    }

}
