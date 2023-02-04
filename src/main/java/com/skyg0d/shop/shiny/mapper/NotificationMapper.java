package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationToAllRequest;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NotificationMapper {

    @Autowired
    private UserService userService;

    public static final NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    public abstract NotificationResponse toNotificationResponse(Notification notification);

    @Mapping(source = "userEmail", target = "user", qualifiedByName = "mapUserFromEmail")
    public abstract Notification toNotification(CreateNotificationRequest request);

    @Mapping(source = "user", target = "user")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    public abstract Notification toNotification(CreateNotificationToAllRequest request, User user);

    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

    @Named("mapUserFromEmail")
    protected User mapUserFromEmail(String email) {
        return userService.findByEmail(email);
    }

}
