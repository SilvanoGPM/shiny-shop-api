package com.skyg0d.shop.shiny.util.notification;

import com.skyg0d.shop.shiny.model.Notification;

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

}
