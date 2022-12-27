package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static com.skyg0d.shop.shiny.util.notification.NotificationCreator.createNotification;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for NotificationRepository")
public class NotificaionRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @DisplayName("countAllByUserAndCanceledAtIsNullAndReadAtIsNull Returns Total Of Notifications When Successful")
    @Test
    void countAllByUserAndCanceledAtIsNullAndReadAtIsNull_ReturnsTotalOfNotifications_WhenSuccessful() {
        Notification notification = persistNotification();

        int count = notificationRepository.countAllByUserAndCanceledAtIsNullAndReadAtIsNull(notification.getUser());

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("findByUserAndCanceledAtIsNullAndReadAtIsNull Returns List Of Notifications Inside PageObject When Successful")
    @Test
    void findByUserAndCanceledAtIsNullAndReadAtIsNull_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification notification = persistNotification();

        Page<Notification> page = notificationRepository.findByUserAndCanceledAtIsNullAndReadAtIsNull(PageRequest.of(0, 1), notification.getUser());

        assertThat(page).isNotNull();

        assertThat(page.getContent()).contains(notification);
    }

    @DisplayName("findByUserAndCanceledAtIsNullAndReadAtIsNotNull Returns List Of Notifications Inside PageObject When Successful")
    @Test
    void findByUserAndCanceledAtIsNullAndReadAtIsNotNull_ReturnsListOfNotificationsInsidePageObject_WhenSuccessful() {
        Notification notification = persistNotification(LocalDateTime.now());

        Page<Notification> page = notificationRepository.findByUserAndCanceledAtIsNullAndReadAtIsNotNull(PageRequest.of(0, 1), notification.getUser());

        assertThat(page).isNotNull();

        assertThat(page.getContent()).contains(notification);
    }

    private Notification persistNotification() {
        return persistNotification(null);
    }

    private Notification persistNotification(LocalDateTime readAt) {
        Notification notificationToBeSave = createNotification();

        User userToBeSave = notificationToBeSave.getUser();

        List<Role> rolesSaved = roleRepository.saveAllAndFlush(userToBeSave.getRoles());

        userToBeSave.setRoles(new HashSet<>(rolesSaved));

        User userSaved = userRepository.save(userToBeSave);

        notificationToBeSave.setUser(userSaved);
        notificationToBeSave.setReadAt(readAt);

        return notificationRepository.save(notificationToBeSave);
    }

}
