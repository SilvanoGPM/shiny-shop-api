package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.PermissionInsufficient;
import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.response.CountNotificationsResponse;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.repository.NotificationRepository;
import com.skyg0d.shop.shiny.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.notification.NotificationCreator.*;
import static com.skyg0d.shop.shiny.util.user.UserCreator.createUser;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for NotificationService")
public class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    AuthUtils authUtils;

    @Mock
    UserService userService;

    @BeforeEach
    void setUp() {
        Page<Notification> unreadNotificationsPage = new PageImpl<>(List.of(createNotification()));

        Notification notification = createNotification();
        notification.setReadAt(LocalDateTime.now());

        Page<Notification> readNotificationsPage = new PageImpl<>(List.of(notification));

        BDDMockito
                .when(userService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(createUser());

        BDDMockito
                .when(notificationRepository.findByUserAndCanceledAtIsNullAndReadAtIsNull(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.any(User.class)))
                .thenReturn(unreadNotificationsPage);

        BDDMockito
                .when(notificationRepository.findByUserAndCanceledAtIsNullAndReadAtIsNotNull(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.any(User.class)))
                .thenReturn(readNotificationsPage);

        BDDMockito
                .when(notificationRepository.findAll(ArgumentMatchers.<Specification<Notification>>any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(unreadNotificationsPage);

        BDDMockito
                .when(notificationRepository.countAllByUserAndCanceledAtIsNullAndReadAtIsNull(ArgumentMatchers.any(User.class)))
                .thenReturn(1);

        BDDMockito
                .when(userService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(createUser());

        BDDMockito
                .when(userService.listAll())
                .thenReturn(List.of(createUser()));

        BDDMockito
                .when(notificationRepository.save(ArgumentMatchers.any(Notification.class)))
                .thenReturn(createNotification());

        BDDMockito
                .when(authUtils.isOwnerOrStaff(ArgumentMatchers.anyString()))
                .thenReturn(true);

        BDDMockito
                .when(notificationRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(createNotification()));
    }

    @Test
    @DisplayName("listAllByUserUnread Returns List Of Notifications Inside Page Object When Successful")
    void listAllByUserUnread_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = createNotification();

        Page<NotificationResponse> notificationsPage = notificationService.listAllByUserUnread(PageRequest.of(0, 1), expectedNotification.getUser().getEmail());

        assertThat(notificationsPage).isNotNull();

        assertThat(notificationsPage.getContent()).isNotEmpty();

        assertThat(notificationsPage.getContent().get(0)).isNotNull();

        assertThat(notificationsPage.getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());

        assertThat(notificationsPage.getContent().get(0).getReadAt()).isNull();
    }

    @Test
    @DisplayName("listAllByUserRead Returns List Of Notifications Inside Page Object When Successful")
    void listAllByUserRead_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = createNotification();

        Page<NotificationResponse> notificationsPage = notificationService.listAllByUserRead(PageRequest.of(0, 1), expectedNotification.getUser().getEmail());

        assertThat(notificationsPage).isNotNull();

        assertThat(notificationsPage.getContent()).isNotEmpty();

        assertThat(notificationsPage.getContent().get(0)).isNotNull();

        assertThat(notificationsPage.getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());

        assertThat(notificationsPage.getContent().get(0).getReadAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("countAllByUser Returns Total Of Notifications When Successful")
    void countAllByUser_ReturnsTotalOfNotifications_WhenSuccessful() {
        CountNotificationsResponse count = notificationService.countAllByUser(createNotification().getUser().getEmail());

        assertThat(count).isNotNull();

        assertThat(count.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("search Returns List Of Notifications Inside Page Object When Successful")
    void search_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = createNotification();

        Page<NotificationResponse> notificationsPage = notificationService.search(PageRequest.of(0, 1), createNotificationParameterSearch());

        assertThat(notificationsPage).isNotNull();

        assertThat(notificationsPage.getContent()).isNotEmpty();

        assertThat(notificationsPage.getContent().get(0)).isNotNull();

        assertThat(notificationsPage.getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());
    }

    @Test
    @DisplayName("create Persists Notification When Successful")
    void create_PersistsNotification_WhenSuccessful() {
        Notification expectedNotification = createNotification();

        NotificationResponse createdNotification = notificationService.create(createCreateNotificationRequest());

        assertThat(createdNotification).isNotNull();

        assertThat(createdNotification.getContent()).isEqualTo(expectedNotification.getContent());
    }

    @Test
    @DisplayName("createToAllUsers Persists Notification To All Users When Successful")
    void createToAllUsers_PersistsNotificationToAllUsers_WhenSuccessful() {
        assertThatCode(() -> notificationService.createToAllUsers(createCreateNotificationToAllRequest()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("read Updates Read At Notification To Now When Successful")
    void read_UpdatesReadAtNotificationToNow_WhenSuccessful() {
        assertThatCode(() -> notificationService.read(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("read Throws PermissionInsufficient When User Has No Permission")
    void read_ThrowsPermissionInsufficient_WhenUserHasNoPermission() {
        BDDMockito
                .when(authUtils.isOwnerOrStaff(ArgumentMatchers.anyString()))
                .thenReturn(false);

        assertThatExceptionOfType(PermissionInsufficient.class)
                .isThrownBy(() -> notificationService.read(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("unread Updates Read At Notification To Null When Successful")
    void unread_UpdatesReadAtNotificationToNull_WhenSuccessful() {
        assertThatCode(() -> notificationService.unread(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("unread Throws PermissionInsufficient When User Has No Permission")
    void unread_ThrowsPermissionInsufficient_WhenUserHasNoPermission() {
        BDDMockito
                .when(authUtils.isOwnerOrStaff(ArgumentMatchers.anyString()))
                .thenReturn(false);

        assertThatExceptionOfType(PermissionInsufficient.class)
                .isThrownBy(() -> notificationService.unread(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("cancel Updates Canceled At Notification To Now When Successful")
    void cancel_UpdatesCanceledAtNotificationToNow_WhenSuccessful() {
        assertThatCode(() -> notificationService.cancel(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

}
