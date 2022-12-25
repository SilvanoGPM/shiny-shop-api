package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.payload.CreateNotificationParams;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    public static final NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    public abstract NotificationResponse toNotificationResponse(Notification notification);

    public abstract Notification toNotification(CreateNotificationParams params);

    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

}
