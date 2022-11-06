package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Order;
import com.skyg0d.shop.shiny.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByUser(Pageable pageable, User user);


}
