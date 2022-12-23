package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {

    public static final NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    public abstract NotificationResponse toNotificationResponse(Notification notification);

    @Mapping(target = "user", ignore = true)
    public abstract Notification toNotification(CreateNotificationRequest request);

    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

}
