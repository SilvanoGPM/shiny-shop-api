package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.PermissionInsufficient;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.mapper.NotificationMapper;
import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.CreateNotificationParams;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationToAllRequest;
import com.skyg0d.shop.shiny.payload.response.CountNotificationsResponse;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.payload.search.NotificationParameterSearch;
import com.skyg0d.shop.shiny.repository.NotificationRepository;
import com.skyg0d.shop.shiny.repository.specification.NotificationSpecification;
import com.skyg0d.shop.shiny.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final AuthUtils authUtils;

    private final UserService userService;

    private final NotificationMapper mapper = NotificationMapper.INSTANCE;

    public Page<NotificationResponse> listAllByUserUnread(Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);

        return notificationRepository
                .findByUserAndCanceledAtIsNullAndReadAtIsNull(pageable, user)
                .map(mapper::toNotificationResponse);
    }

    public Page<NotificationResponse> listAllByUserRead(Pageable pageable, String userEmail) {
        User user = userService.findByEmail(userEmail);

        return notificationRepository
                .findByUserAndCanceledAtIsNullAndReadAtIsNotNull(pageable, user)
                .map(mapper::toNotificationResponse);
    }

    public CountNotificationsResponse countAllByUser(String userEmail) {
        User user = userService.findByEmail(userEmail);

        int count = notificationRepository.countAllByUserAndCanceledAtIsNullAndReadAtIsNull(user);

        return CountNotificationsResponse
                .builder()
                .count(count)
                .build();
    }

    public Page<NotificationResponse> search(Pageable pageable, NotificationParameterSearch search) {
        return notificationRepository
                .findAll(NotificationSpecification.getSpecification(search), pageable)
                .map(mapper::toNotificationResponse);
    }

    public NotificationResponse create(CreateNotificationRequest request) {
        User user = userService.findByEmail(request.getUserEmail());

        return create(CreateNotificationParams.fromRequest(request, user));
    }

    public void createToAllUsers(CreateNotificationToAllRequest request) {
        userService.listAll().forEach((user) ->
                create(CreateNotificationParams.fromRequest(request, user))
        );
    }

    public void read(String id) {
        Notification notification = findById(id);

        validateUser(notification);

        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public void unread(String id) {
        Notification notification = findById(id);

        validateUser(notification);

        notification.setReadAt(null);

        notificationRepository.save(notification);
    }

    public void cancel(String id) {
        Notification notification = findById(id);

        notification.setCanceledAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public void validateUser(Notification notification) {
        boolean isOwnerOrStaff = authUtils.isOwnerOrStaff(notification.getUser().getEmail());

        if (!isOwnerOrStaff) {
            throw new PermissionInsufficient("notification");
        }
    }

    private Notification findById(String id) throws ResourceNotFoundException {
        return notificationRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    private NotificationResponse create(CreateNotificationParams params) {
        Notification notification = mapper.toNotification(params);

        return mapper.toNotificationResponse(notificationRepository.save(notification));
    }

}
