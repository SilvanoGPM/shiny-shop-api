package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
