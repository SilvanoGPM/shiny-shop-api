package com.skyg0d.shop.shiny.mapper;

import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.ReplaceUserRequest;
import com.skyg0d.shop.shiny.payload.request.SignupRequest;
import com.skyg0d.shop.shiny.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "user.roles", qualifiedByName = "mapRoles")
    public abstract UserResponse toUserResponse(User user);

    @Mapping(target = "password", source = "request.password", qualifiedByName = "encodePassword")
    public abstract User toUser(SignupRequest request);

    public abstract User toUser(ReplaceUserRequest request);

    @Named("mapRoles")
    Set<String> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

    @Named("encodePassword")
    String encodePassword(String password) {
        if (password != null && !password.isEmpty()) {
            return new BCryptPasswordEncoder().encode(password);
        }

        return password;
    }

}
