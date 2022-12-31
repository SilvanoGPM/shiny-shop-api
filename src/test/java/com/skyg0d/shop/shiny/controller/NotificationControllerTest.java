package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.response.CountNotificationsResponse;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.payload.search.NotificationParameterSearch;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.NotificationService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.user.UserCreator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.skyg0d.shop.shiny.util.notification.NotificationCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for NotificationController")
public class NotificationControllerTest {

    @InjectMocks
    NotificationController notificationController;

    @Mock
    NotificationService notificationService;

    @Mock
    AuthUtils authUtils;

    @BeforeEach
    void setUp() {
        Page<NotificationResponse> unreadNotificationsPage = new PageImpl<>(List.of(createNotificationResponse()));

        NotificationResponse notification = createNotificationResponse();
        notification.setReadAt(LocalDateTime.now());

        Page<NotificationResponse> readNotificationsPage = new PageImpl<>(List.of(notification));

        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UserCreator.ID, UserCreator.USERNAME, UserCreator.EMAIL, UserCreator.PASSWORD, List.of(new SimpleGrantedAuthority("ADMIN"))));

        BDDMockito
                .when(notificationService.listAllByUserUnread(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.anyString()))
                .thenReturn(unreadNotificationsPage);

        BDDMockito
                .when(notificationService.listAllByUserRead(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.anyString()))
                .thenReturn(readNotificationsPage);

        BDDMockito
                .when(notificationService.search(ArgumentMatchers.any(Pageable.class), ArgumentMatchers.any(NotificationParameterSearch.class)))
                .thenReturn(unreadNotificationsPage);

        BDDMockito
                .when(notificationService.countAllByUser(ArgumentMatchers.anyString()))
                .thenReturn(new CountNotificationsResponse(1));

        BDDMockito
                .when(notificationService.create(ArgumentMatchers.any(CreateNotificationRequest.class)))
                .thenReturn(createNotificationResponse());
    }

    @Test
    @DisplayName("listAllByUserUnread Returns List Of Notifications Inside Page Object When Successful")
    void listAllByUserUnread_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        NotificationResponse expectedNotification = createNotificationResponse();

        ResponseEntity<Page<NotificationResponse>> entity = notificationController.listAllByUserUnread(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());

        assertThat(entity.getBody().getContent().get(0).getReadAt()).isNull();
    }

    @Test
    @DisplayName("listAllByUserRead Returns List Of Notifications Inside Page Object When Successful")
    void listAllByUserRead_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        NotificationResponse expectedNotification = createNotificationResponse();

        ResponseEntity<Page<NotificationResponse>> entity = notificationController.listAllByUserRead(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());

        assertThat(entity.getBody().getContent().get(0).getReadAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("countAllByUser Returns Total Of Notifications When Successful")
    void countAllByUser_ReturnsTotalOfNotifications_WhenSuccessful() {
        ResponseEntity<CountNotificationsResponse> entity = notificationController.countAllByUser();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("search Returns List Of Notifications Inside Page Object When Successful")
    void search_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = createNotification();

        ResponseEntity<Page<NotificationResponse>> entity = notificationController.search(PageRequest.of(0, 1), createNotificationParameterSearch());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getContent()).isEqualTo(expectedNotification.getContent());
    }

    @Test
    @DisplayName("create Persists Notification When Successful")
    void create_PersistsNotification_WhenSuccessful() {
        NotificationResponse expectedNotification = createNotificationResponse();

        ResponseEntity<NotificationResponse> entity = notificationController.create(createCreateNotificationRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getContent()).isEqualTo(expectedNotification.getContent());
    }

    @Test
    @DisplayName("createToAll Persists Notification To All Users When Successful")
    void createToAllUsers_PersistsNotificationToAllUsers_WhenSuccessful() {
        ResponseEntity<Void> entity = notificationController.createToAll(createCreateNotificationToAllRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("read Updates Read At Notification To Now When Successful")
    void read_UpdatesReadAtNotificationToNow_WhenSuccessful() {
        ResponseEntity<Void> entity = notificationController.read(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("unread Updates Read At Notification To Null When Successful")
    void unread_UpdatesReadAtNotificationToNull_WhenSuccessful() {
        ResponseEntity<Void> entity = notificationController.unread(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("cancel Updates Canceled At Notification To Now When Successful")
    void cancel_UpdatesCanceledAtNotificationToNow_WhenSuccessful() {
        ResponseEntity<Void> entity = notificationController.cancel(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
