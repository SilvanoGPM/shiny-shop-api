package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(ERole name);
}
