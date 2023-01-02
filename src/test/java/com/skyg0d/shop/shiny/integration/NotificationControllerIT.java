package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateNotificationRequest;
import com.skyg0d.shop.shiny.payload.response.CountNotificationsResponse;
import com.skyg0d.shop.shiny.payload.response.NotificationResponse;
import com.skyg0d.shop.shiny.repository.NotificationRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static com.skyg0d.shop.shiny.util.notification.NotificationCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for NotificationController")
public class NotificationControllerIT {

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTCreator jwtCreator;

    @Test
    @DisplayName("listAllByUserUnread Returns List Of Notifications Inside Page Object When Successful")
    void listAllByUserUnread_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = persistUnreadNotification();

        ResponseEntity<PageableResponse<NotificationResponse>> entity = httpClient.exchange(
                "/notifications/unread",
                HttpMethod.GET,
                jwtCreator.createUserAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

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
        Notification expectedNotification = persistReadNotification();

        ResponseEntity<PageableResponse<NotificationResponse>> entity = httpClient.exchange(
                "/notifications/read",
                HttpMethod.GET,
                jwtCreator.createUserAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

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
        persistUnreadNotification();

        ResponseEntity<CountNotificationsResponse> entity = httpClient.exchange(
                "/notifications/count",
                HttpMethod.GET,
                jwtCreator.createUserAuthEntity(null),
                CountNotificationsResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("search Returns List Of Notifications Inside Page Object When Successful")
    void search_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification expectedNotification = persistUnreadNotification();

        ResponseEntity<PageableResponse<NotificationResponse>> entity = httpClient.exchange(
                "/notifications/search",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

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

        CreateNotificationRequest request = createCreateNotificationRequest();
        request.setUserEmail(findUser().getEmail());

        ResponseEntity<NotificationResponse> entity = httpClient.exchange(
                "/notifications",
                HttpMethod.POST,
                jwtCreator.createAdminAuthEntity(request),
                NotificationResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getContent()).isEqualTo(expectedNotification.getContent());
    }

    @Test
    @DisplayName("createToAll Persists Notification To All Users When Successful")
    void createToAllUsers_PersistsNotificationToAllUsers_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/notifications/all",
                HttpMethod.POST,
                jwtCreator.createAdminAuthEntity(createCreateNotificationToAllRequest()),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("read Updates Read At Notification To Now When Successful")
    void read_UpdatesReadAtNotificationToNow_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/notifications/{id}/read",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistUnreadNotification().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("read Returns ExceptionDetails When Permission Is Insufficient")
    void read_ReturnsExceptionDetails_WhenPermissionIsInsufficient() {
        String expectedTitle = "Permission Insufficient";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/notifications/{id}/read",
                HttpMethod.PATCH,
                jwtCreator.createOtherUserAuthEntity(null),
                ExceptionDetails.class,
                persistReadNotification().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("unread Updates Read At Notification To Null When Successful")
    void unread_UpdatesReadAtNotificationToNull_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/notifications/{id}/unread",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistReadNotification().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("unread Returns ExceptionDetails When Permission Is Insufficient")
    void unread_ReturnsExceptionDetails_WhenPermissionIsInsufficient() {
        String expectedTitle = "Permission Insufficient";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/notifications/{id}/unread",
                HttpMethod.PATCH,
                jwtCreator.createOtherUserAuthEntity(null),
                ExceptionDetails.class,
                persistReadNotification().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("cancel Updates Canceled At Notification To Now When Successful")
    void cancel_UpdatesCanceledAtNotificationToNow_WhenSuccessful() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/notifications/{id}/cancel",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                persistUnreadNotification().getId()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private Notification persistUnreadNotification() {
        Notification notification = createNotification();

        notification.setUser(userRepository.findByEmail(jwtCreator.createUser().getEmail()).orElseThrow());

        return notificationRepository.save(notification);
    }

    private Notification persistReadNotification() {
        Notification notification = createNotification();

        notification.setReadAt(LocalDateTime.now());

        notification.setUser(findUser());

        return notificationRepository.save(notification);
    }

    private User findUser() {
        return userRepository
                .findByEmail(jwtCreator.createUser().getEmail())
                .orElseThrow();
    }

}
