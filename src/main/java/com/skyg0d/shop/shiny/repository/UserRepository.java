package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

}
