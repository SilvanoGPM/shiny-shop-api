package com.skyg0d.shop.shiny.util.user;

import com.skyg0d.shop.shiny.mapper.UserMapper;
import com.skyg0d.shop.shiny.model.ERole;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.PromoteRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceUserRequest;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import com.skyg0d.shop.shiny.payload.search.UserParameterSearch;

import java.util.Set;
import java.util.UUID;

public class UserCreator {

    public static final UUID ID = UUID.fromString("eaf90e2e-ebe7-4c60-8a16-d7f4aa14b730");
    public static final String USERNAME = "SkyG0D";
    public static final String FULL_NAME = "Full Name";
    public static final String EMAIL = "test@mail.com";
    public static final String PASSWORD = "password";
    public static final Set<Role> ROLES = Set.of(new Role(ERole.ROLE_USER));

    public static User createUserToBeSave() {
        return User
                .builder()
                .username(USERNAME)
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .roles(ROLES)
                .build();
    }

    public static User createUser() {
        return User
                .builder()
                .id(ID)
                .fullName(FULL_NAME)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .roles(ROLES)
                .build();
    }

    public static UserResponse createUserResponse() {
        return UserMapper.INSTANCE.toUserResponse(createUser());
    }

    public static PromoteRequest createPromoteRequest() {
        return PromoteRequest
                .builder()
                .email(EMAIL)
                .roles(Set.of("admin"))
                .build();


    }

    public static ReplaceUserRequest createReplaceUserRequest() {
        return ReplaceUserRequest
                .builder()
                .email(EMAIL)
                .fullName(FULL_NAME)
                .username(USERNAME)
                .build();
    }

    public static UserParameterSearch createUserParameterSearch() {
        return UserParameterSearch
                .builder()
                .fullName(FULL_NAME)
                .build();
    }

}
