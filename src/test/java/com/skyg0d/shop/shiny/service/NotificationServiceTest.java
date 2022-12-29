package com.skyg0d.shop.shiny.service;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.skyg0d.shop.shiny.util.notification.NotificationCreator.createNotification;
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
                .when(notificationRepository.countAllByUserAndCanceledAtIsNullAndReadAtIsNull(ArgumentMatchers.any(User.class)))
                .thenReturn(1);
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

}
