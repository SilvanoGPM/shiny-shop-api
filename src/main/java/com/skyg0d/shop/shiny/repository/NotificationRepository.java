package com.skyg0d.shop.shiny.repository;


import com.skyg0d.shop.shiny.model.Notification;
import com.skyg0d.shop.shiny.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {

    int countAllByUserAndCanceledAtIsNullAndReadAtIsNull(User user);

    Page<Notification> findByUserAndCanceledAtIsNullAndReadAtIsNull(Pageable pageable, User user);

    Page<Notification> findByUserAndCanceledAtIsNullAndReadAtIsNotNull(Pageable pageable, User user);

}
